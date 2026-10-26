package com.jerry.wallet.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WalletTransferDto(@NotNull(message = "wallet funding amount cannot be null") @Positive @Digits(integer = 15, fraction = 4) BigDecimal amount,
                                @NotNull(message = "recipient wallet id cannot be null") String recipientWalletId) {
}
