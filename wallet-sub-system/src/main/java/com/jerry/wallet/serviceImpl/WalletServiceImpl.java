package com.jerry.wallet.serviceImpl;

import com.jerry.wallet.dto.CreateWalletDto;
import com.jerry.wallet.dto.FundWalletDto;
import com.jerry.wallet.dto.WalletTransferDto;
import com.jerry.wallet.exception.ErrorResponse;
import com.jerry.wallet.model.Wallets;
import com.jerry.wallet.pojo.WalletPojo;
import com.jerry.wallet.repository.WalletsRepository;
import com.jerry.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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

    /**
     * Create a wallet for a user
     */
    @Override
    public void createWalletForUser(CreateWalletDto createWalletDto) {
        Wallets wallets = new Wallets();
        wallets.setUserId(createWalletDto.userId());//in a case where the user entity us available i would check if the user exists
        wallets.setBalance(BigDecimal.ZERO);
        wallets.setVersion(BigInteger.ONE); //TODO: implement optimistic locking properly

        walletsRepository.save(wallets);
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
    public void transferFunds(String walletId, WalletTransferDto walletTransferDto) {
        Wallets sourceWallet = this.validateWalletExists(walletId);

        Wallets recipientWallet = walletsRepository.findById(walletTransferDto.recipientWalletId())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, "Recipient sourceWallet not found"));

        if (sourceWallet.getBalance().compareTo(walletTransferDto.amount()) < 0) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Insufficient funds in sourceWallet");
        }
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(walletTransferDto.amount()));
        walletsRepository.save(sourceWallet);

        recipientWallet.setBalance(recipientWallet.getBalance().add(walletTransferDto.amount()));
        walletsRepository.save(recipientWallet);
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
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, "Wallet not found"));
    }
}
