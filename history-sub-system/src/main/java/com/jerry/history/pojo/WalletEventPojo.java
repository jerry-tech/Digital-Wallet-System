package com.jerry.history.pojo;


import com.jerry.history.enumeration.TransactionType;
import com.jerry.history.enumeration.WalletEventTypeConstant;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletEventPojo(
        String eventId,                 // unique per event, so the History service can dedupe
        WalletEventTypeConstant eventType,
        String walletId,
        String userId,
        String counterpartyWalletId,    // the other side of a transfer, null otherwise
        String amount,                  // the amount moved, not the balance; string as in the README
        String transactionId,           // id of the wallet_transactions row
        String transferId,              // shared by both legs of a transfer
        TransactionType transactionType,
        Instant timestamp,              // UTC
        int schemaVersion) {
}