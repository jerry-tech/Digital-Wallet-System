package com.jerry.wallet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing wallet transactions such as funding and transfers.
 * @author Jeremiah Udoh
 * @since 27th May 2026
 */
@Entity
@Getter
@Setter
public class WalletTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Wallets wallet;

    //Added these two new fields to track transfer transactions, which also covers the audit part of issue 7:
    @Column(length = 36)
    private String transferId;

    @Column(precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    public enum TransactionType {
        FUND,
        TRANSFER_OUT,
        TRANSFER_IN
    }

    public enum TransactionStatus {
        COMPLETED,
        FAILED
    }

}
