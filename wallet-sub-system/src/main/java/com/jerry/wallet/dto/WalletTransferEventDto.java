package com.jerry.wallet.dto;

import com.jerry.wallet.enumuration.WalletEventTypeConstant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransferEventDto(WalletEventTypeConstant eventType, String walletId,
                                     String userId, BigDecimal amount, String transactionId, LocalDateTime timestamp) {
}

