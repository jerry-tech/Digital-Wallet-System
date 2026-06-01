package com.jerry.wallet.service;

import com.jerry.wallet.dto.CreateWalletDto;
import com.jerry.wallet.dto.FundWalletDto;
import com.jerry.wallet.dto.WalletTransferDto;
import com.jerry.wallet.model.Wallets;
import com.jerry.wallet.pojo.WalletPojo;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    void createWalletForUser(CreateWalletDto createWalletDto);

    void fundWallet(String walletId, FundWalletDto fundWalletDto);

    void transferFunds(String walletId, WalletTransferDto walletTransferDto);

    BigDecimal getBalance(String walletId);

    List<WalletPojo> getUserWallets(@NotNull String userId, Long limit, Long offset);
}
