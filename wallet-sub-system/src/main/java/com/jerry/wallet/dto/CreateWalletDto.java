package com.jerry.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWalletDto(@NotBlank @Size(max = 100) String userId) {
}
