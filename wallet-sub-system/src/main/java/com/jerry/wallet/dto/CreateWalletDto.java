package com.jerry.wallet.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWalletDto(@NotBlank String userId) {
}
