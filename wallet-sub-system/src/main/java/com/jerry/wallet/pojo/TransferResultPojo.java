package com.jerry.wallet.pojo;

import java.math.BigDecimal;

public record TransferResultPojo(String transferId, String transactionId,
                                 BigDecimal sourceBalance, BigDecimal recipientBalance) {}