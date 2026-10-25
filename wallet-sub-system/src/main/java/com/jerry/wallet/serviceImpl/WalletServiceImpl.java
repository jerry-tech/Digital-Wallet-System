package com.jerry.wallet.serviceImpl;

import com.jerry.wallet.config.KafkaTopicProperties;
import com.jerry.wallet.dto.*;
import com.jerry.wallet.enumuration.WalletEventTypeEnum;
import com.jerry.wallet.exception.ErrorResponse;
import com.jerry.wallet.model.WalletTransactions;
import com.jerry.wallet.model.Wallets;
import com.jerry.wallet.pojo.WalletPojo;
import com.jerry.wallet.repository.WalletsRepository;
import com.jerry.wallet.service.WalletService;
import com.jerry.wallet.service.WalletTransactionService;
import com.jerry.wallet.service.kafka.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of WalletService interface
 *
 * @author Jeremiah Udoh
 * @since 1st June 2026
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletsRepository walletsRepository;
    private final KafkaPublisherService kafkaPublisherService;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final WalletTransactionService walletTransactionService;

    /**
     * Create a wallet for a user
     */
    @Override
    @Transactional
    public void createWalletForUser(CreateWalletDto createWalletDto) {
        String transactionId = java.util.UUID.randomUUID().toString();

        Wallets wallets = new Wallets();
        wallets.setUserId(createWalletDto.userId());//in a case where the user entity us available i would check if the user exists
        wallets.setBalance(BigDecimal.ZERO);

        walletsRepository.save(wallets);

        kafkaPublisherService.sendMessage("Wallet created for user: " + createWalletDto.userId(), kafkaTopicProperties.getWalletCreationTopic());
    }

    /**
     * Add money to wallet
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fundWallet(String walletId, FundWalletDto fundWalletDto) {
        Wallets wallet = this.validateWalletExists(walletId);
        wallet.setBalance(wallet.getBalance().add(fundWalletDto.amount()));
        walletsRepository.save(wallet);
    }

    /**
     * Send money to another wallet
     */
    @Override
    @Transactional
    public void transferFunds(String walletFromId, WalletTransferDto walletTransferDto) {
        String transactionId = java.util.UUID.randomUUID().toString();

        // 1. Lock both wallets (ORDER BY id to prevent deadlock)
        List<Wallets> listOfWalletsForUpdate = walletsRepository.findWalletsForUpdate(List.of(walletFromId, walletTransferDto.recipientWalletId()));

        if (listOfWalletsForUpdate.size() != 2) {
            throw new ErrorResponse(HttpStatus.NOT_FOUND, "One or both wallets not found for transfer");
        }

        Map<String, Wallets> walletsById = listOfWalletsForUpdate.stream()
                .collect(Collectors.toMap(Wallets::getId, Function.identity()));

        Wallets sourceWallet = walletsById.get(walletFromId);
        Wallets recipientWallet = walletsById.get(walletTransferDto.recipientWalletId());

        // 2. checking if the source wallet has sufficient funds for the transfer(Check A has >= $30)
        if (sourceWallet.getBalance().compareTo(walletTransferDto.amount()) < 0) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Insufficient funds in sourceWallet");
        }
        // 3. Update both balances.
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(walletTransferDto.amount()));
        recipientWallet.setBalance(recipientWallet.getBalance().add(walletTransferDto.amount()));

        // 4. save wallets transaction
        walletTransactionService.createWalletTransaction(new WalletTransactionDto(
                sourceWallet,
                walletTransferDto.amount(),
                WalletTransactions.TransactionType.TRANSFER_OUT,
                WalletTransactions.TransactionStatus.COMPLETED
        ));

        walletTransactionService.createWalletTransaction(new WalletTransactionDto(
                recipientWallet,
                walletTransferDto.amount(),
                WalletTransactions.TransactionType.TRANSFER_IN,
                WalletTransactions.TransactionStatus.COMPLETED
        ));

        // 5. Save both wallets.
        walletTransactionService.handleWalletTransactionPublishingEvent(
                new WalletTransferEventDto(WalletEventTypeEnum.TRANSFER_COMPLETED, sourceWallet.getId(), sourceWallet.getUserId(), walletTransferDto.amount(), transactionId, LocalDateTime.now()));


        // 6. Commit
        walletsRepository.saveAll(listOfWalletsForUpdate);
    }

    /**
     * check the balance of a wallet
     */
    @Override
    public BigDecimal getBalance(String walletId) {
        Wallets wallet = this.validateWalletExists(walletId);
        return wallet.getBalance();
    }

    /**
     * Retrieve wallets for a user with pagination
     */
    @Override
    public List<WalletPojo> getUserWallets(String userId, Long limit, Long offset) {
        return walletsRepository.findWalletsByUserId(userId, PageRequest.of(offset.intValue(), limit.intValue()))
                .stream()
                .map(x -> new WalletPojo(x.getBalance(), x.getCreatedAt(), x.getVersion()))
                .toList();
    }


    private Wallets validateWalletExists(String walletId) {
        return walletsRepository.findById(walletId)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, "Wallet not found for id: " + walletId));
    }
}
