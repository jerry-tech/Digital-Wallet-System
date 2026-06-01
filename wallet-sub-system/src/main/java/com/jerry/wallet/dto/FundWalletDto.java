package com.jerry.wallet.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FundWalletDto(@NotNull(message = "wallet funding amount cannot be null") BigDecimal amount) { }
