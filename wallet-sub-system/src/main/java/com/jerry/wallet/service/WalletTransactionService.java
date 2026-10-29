package com.jerry.wallet.service;


import com.jerry.wallet.dto.WalletTransactionDto;
import com.jerry.wallet.model.WalletTransactions;

public interface WalletTransactionService {
    WalletTransactions createWalletTransaction(WalletTransactionDto walletTransactionDto);
}
