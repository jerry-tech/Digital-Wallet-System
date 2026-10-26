package com.jerry.wallet.serviceImpl;

import com.jerry.wallet.dto.*;
import com.jerry.wallet.enumuration.WalletEventTypeConstant;
import com.jerry.wallet.exception.ErrorResponse;
import com.jerry.wallet.exception.InsufficientFundsException;
import com.jerry.wallet.model.WalletTransactions;
import com.jerry.wallet.model.Wallets;
import com.jerry.wallet.pojo.WalletPojo;
import com.jerry.wallet.pojo.TransferResultPojo;
import com.jerry.wallet.pojo.WalletTransactionPojo;
import com.jerry.wallet.repository.WalletsRepository;
import com.jerry.wallet.service.WalletService;
import com.jerry.wallet.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
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
    private final WalletTransactionService walletTransactionService;
    private final ApplicationEventPublisher events;
    private final TransactionTemplate transactionTemplate;
    private static final int MAX_FUND_ATTEMPTS = 5;

    /**
     * Create a wallet for a user
     */
    @Override
    @Transactional
    public WalletPojo createWalletForUser(CreateWalletDto createWalletDto) {

        Wallets wallet = new Wallets();
        wallet.setUserId(createWalletDto.userId());//in a case where the user entity us available i would check if the user exists
        wallet.setBalance(BigDecimal.ZERO);

        walletsRepository.save(wallet);

        events.publishEvent(WalletEventDto.of(WalletEventTypeConstant.WALLET_CREATED, wallet, null,
                BigDecimal.ZERO, null, null, null));

        return new WalletPojo(wallet);
    }

    /**
     * Add money to wallet
     */

    // ---- fund: optimistic locking + bounded retry (issue 3) ----
    @Override
    public WalletTransactionPojo fundWallet(String walletId, FundWalletDto dto) {
        for (int attempt = 1; ; attempt++) {
            try {
                return transactionTemplate.execute(status -> doFund(walletId, dto));
            } catch (OptimisticLockingFailureException e) {
                if (attempt >= MAX_FUND_ATTEMPTS) {
                    throw new ErrorResponse(HttpStatus.CONFLICT, "Wallet is busy, please retry");
                }
                backoff(attempt);
            }
        }
    }

    private WalletTransactionPojo doFund(String walletId, FundWalletDto dto) {
        Wallets wallet = validateWalletExists(walletId);
        wallet.setBalance(wallet.getBalance().add(dto.amount()));      // flushed at commit; @Version is checked there

        WalletTransactions txn = walletTransactionService.createWalletTransaction(new WalletTransactionDto(
                wallet, dto.amount(), WalletTransactions.TransactionType.FUND, WalletTransactions.TransactionStatus.COMPLETED, null, wallet.getBalance()));

        events.publishEvent(WalletEventDto.of(WalletEventTypeConstant.WALLET_FUNDED, wallet, null,
                dto.amount(), txn.getId(), null, WalletTransactions.TransactionType.FUND));              // amount funded, real txn id

        return new WalletTransactionPojo(txn.getId(), wallet.getId(), wallet.getBalance());
    }


    @Override
    public TransferResultPojo transferFunds(String walletFromId, WalletTransferDto dto) {
        if (walletFromId.equals(dto.recipientWalletId())) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Source and recipient wallets cannot be the same");
        }
        String transferId = UUID.randomUUID().toString();
        try {
            return transactionTemplate.execute(status -> doTransfer(walletFromId, dto, transferId));
        } catch (InsufficientFundsException e) {
            recordFailedTransfer(walletFromId, dto, transferId);    // the first tx is already rolled back
            throw e;
        }
    }


    private TransferResultPojo doTransfer(String walletFromId, WalletTransferDto walletTransferDto, String transferId) {

        if (walletFromId.equals(walletTransferDto.recipientWalletId())) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Source and recipient wallets cannot be the same");
        }

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
            throw new InsufficientFundsException();
        }

        // 3. Update both balances.
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(walletTransferDto.amount()));
        sourceWallet.setUpdatedAt(LocalDateTime.now());

        recipientWallet.setBalance(recipientWallet.getBalance().add(walletTransferDto.amount()));
        recipientWallet.setUpdatedAt(LocalDateTime.now());

        // 4. save wallets transaction
        WalletTransactions out = walletTransactionService.createWalletTransaction(new WalletTransactionDto(
                sourceWallet,
                walletTransferDto.amount(),
                WalletTransactions.TransactionType.TRANSFER_OUT,
                WalletTransactions.TransactionStatus.COMPLETED,
                transferId, sourceWallet.getBalance()
        ));

        WalletTransactions in = walletTransactionService.createWalletTransaction(new WalletTransactionDto(
                recipientWallet,
                walletTransferDto.amount(),
                WalletTransactions.TransactionType.TRANSFER_IN,
                WalletTransactions.TransactionStatus.COMPLETED,
                transferId, recipientWallet.getBalance()
        ));

        // one event per side, so History can show "A sent" and "B received"
        events.publishEvent(WalletEventDto.of(WalletEventTypeConstant.TRANSFER_COMPLETED, sourceWallet,
                recipientWallet.getId(), walletTransferDto.amount(), out.getId(), transferId, WalletTransactions.TransactionType.TRANSFER_OUT));

        events.publishEvent(WalletEventDto.of(WalletEventTypeConstant.TRANSFER_COMPLETED, recipientWallet,
                sourceWallet.getId(), walletTransferDto.amount(), in.getId(), transferId, WalletTransactions.TransactionType.TRANSFER_IN));


        // 6. Commit
        walletsRepository.saveAll(listOfWalletsForUpdate);

        return new TransferResultPojo(transferId, out.getId(), sourceWallet.getBalance(), recipientWallet.getBalance());
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
    public List<WalletPojo> getUserWallets(String userId, Long limit, Long pageNumber, Sort.Direction sortOrderConstant) {
        Pageable pageable = PageRequest.of(
                pageNumber.intValue(),
                limit.intValue(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return walletsRepository.findWalletsByUserId(userId, pageable)
                .stream()
                .map(WalletPojo::new)
                .toList();
    }


    private Wallets validateWalletExists(String walletId) {
        return walletsRepository.findById(walletId)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, "Wallet not found for id: " + walletId));
    }

    private void backoff(int attempt) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(10L * attempt, 50L * attempt));   // jitter
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted");
        }
    }

    private void recordFailedTransfer(String fromId, WalletTransferDto dto, String transferId) {
        transactionTemplate.executeWithoutResult(status -> walletsRepository.findById(fromId).ifPresent(source -> {
            WalletTransactions failed = walletTransactionService.createWalletTransaction(new WalletTransactionDto(
                    source, dto.amount(), WalletTransactions.TransactionType.TRANSFER_OUT, WalletTransactions.TransactionStatus.FAILED,
                    transferId, source.getBalance()));

            events.publishEvent(WalletEventDto.of(WalletEventTypeConstant.TRANSFER_FAILED, source,
                    dto.recipientWalletId(), dto.amount(), failed.getId(), transferId, WalletTransactions.TransactionType.TRANSFER_OUT));
        }));
    }
}
