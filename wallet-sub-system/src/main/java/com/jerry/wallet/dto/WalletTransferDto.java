package com.jerry.wallet.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WalletTransferDto(@NotNull(message = "wallet funding amount cannot be null") BigDecimal amount,
                                @NotNull(message = "recipient wallet id cannot be null") String recipientWalletId) {
}
