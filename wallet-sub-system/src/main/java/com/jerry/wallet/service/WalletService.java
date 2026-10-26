package com.jerry.wallet.service;

import com.jerry.wallet.dto.CreateWalletDto;
import com.jerry.wallet.dto.FundWalletDto;
import com.jerry.wallet.dto.WalletTransferDto;
import com.jerry.wallet.pojo.WalletPojo;
import com.jerry.wallet.pojo.TransferResultPojo;
import com.jerry.wallet.pojo.WalletTransactionPojo;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    WalletPojo createWalletForUser(CreateWalletDto createWalletDto);

    WalletTransactionPojo fundWallet(String walletId, FundWalletDto fundWalletDto);

    TransferResultPojo transferFunds(String walletId, WalletTransferDto walletTransferDto);

    BigDecimal getBalance(String walletId);

    List<WalletPojo> getUserWallets(@NotNull String userId, Long limit, Long pageNumber, Sort.Direction sortOrderConstant);
}
