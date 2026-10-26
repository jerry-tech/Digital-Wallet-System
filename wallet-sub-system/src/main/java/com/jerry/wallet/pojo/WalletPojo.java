package com.jerry.wallet.pojo;

import com.jerry.wallet.model.Wallets;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class WalletPojo {
    private String id;
    private BigDecimal balance;
    private String userId;

    public WalletPojo(Wallets wallets) {
        this.id = wallets.getId();
        this.balance = wallets.getBalance();
        this.userId = wallets.getUserId();
    }
}
