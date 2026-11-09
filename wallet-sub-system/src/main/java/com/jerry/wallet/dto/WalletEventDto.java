package com.jerry.wallet.dto;

import com.jerry.wallet.enumuration.WalletEventTypeConstant;
import com.jerry.wallet.model.WalletTransactions;
import com.jerry.wallet.model.Wallets;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletEventDto(
        String eventId,                 // unique per event, so the History service can dedupe
        WalletEventTypeConstant eventType,
        String walletId,
        String userId,
        String counterpartyWalletId,    // the other side of a transfer, null otherwise
        String amount,                  // the amount moved, not the balance; string as in the README
        String transactionId,           // id of the wallet_transactions row
        String transferId,              // shared by both legs of a transfer
        WalletTransactions.TransactionType transactionType,
        Instant timestamp,
        int schemaVersion) {

    public static WalletEventDto of(WalletEventTypeConstant type, Wallets wallet, String counterpartyWalletId,
                                    BigDecimal amount, String transactionId, String transferId,
                                    WalletTransactions.TransactionType txType) {

        return new WalletEventDto(UUID.randomUUID().toString(), type, wallet.getId(), wallet.getUserId(),
                counterpartyWalletId, amount.toPlainString(), transactionId,
                transferId, txType, Instant.now(), 1);
    }
}