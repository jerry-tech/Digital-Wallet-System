package com.jerry.wallet.pojo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class WalletPojo {
    private BigDecimal balance;
    private BigInteger version;
    private LocalDateTime createdAt;

    public WalletPojo(BigDecimal balance, LocalDateTime createdAt, BigInteger version) {
        this.balance = balance;
        this.createdAt = createdAt;
        this.version = version;
    }
}
