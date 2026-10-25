package com.jerry.wallet.dto;

import com.jerry.wallet.enumuration.WalletEventTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransferEventDto(WalletEventTypeEnum eventType, String walletId,
                                     String userId, BigDecimal amount, String transactionId, LocalDateTime timestamp) {
}

