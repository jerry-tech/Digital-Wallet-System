package com.jerry.wallet.dto;

import com.jerry.wallet.model.WalletTransactions;
import com.jerry.wallet.model.Wallets;

import java.math.BigDecimal;

public record WalletTransactionDto (Wallets wallet, BigDecimal amount, WalletTransactions.TransactionType transactionType,
                                    WalletTransactions.TransactionStatus transactionStatus, String transferId, BigDecimal balanceAfter) {
}
