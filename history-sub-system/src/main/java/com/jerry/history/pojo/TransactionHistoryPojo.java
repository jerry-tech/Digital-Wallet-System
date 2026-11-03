package com.jerry.history.pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.jerry.history.model.TransactionEvents;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record TransactionHistoryPojo(String eventId,
                                     String eventType,
                                     String walletId,
                                     String userId,
                                     BigDecimal amount,
                                     String transactionId,
                                     LocalDateTime createdAt,
                                     JsonNode eventData) {

    public static TransactionHistoryPojo of(TransactionEvents e, JsonNode eventData) {
        return new TransactionHistoryPojo(e.getId(), e.getEventType(), e.getWalletId(), e.getUserId(),
                e.getAmount(), e.getTransactionId(), e.getCreatedAt(), eventData);
    }
}