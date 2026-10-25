package com.jerry.wallet.service;


import com.jerry.wallet.dto.WalletTransactionDto;
import com.jerry.wallet.dto.WalletTransferEventDto;

public interface WalletTransactionService {

    void handleWalletTransactionPublishingEvent(WalletTransferEventDto walletTransferEventDto);

    void createWalletTransaction(WalletTransactionDto walletTransactionDto);
}
