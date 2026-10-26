package com.jerry.wallet.pojo;

import java.math.BigDecimal;

public record WalletTransactionPojo(String transactionId, String walletId, BigDecimal balance) {}
