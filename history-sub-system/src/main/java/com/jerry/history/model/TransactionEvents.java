package com.jerry.history.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Entity representing transaction events for auditing and tracking purposes.
 * @author Jeremiah Udoh
 * @since 27th May 2026
 */
@Entity
@Table(name = "transaction_events", indexes = {
        @Index(name = "idx_te_wallet_created", columnList = "walletId, createdAt"),
        @Index(name = "idx_te_user_created", columnList = "userId, createdAt")})
@Getter
@Setter
public class TransactionEvents {
    @Id @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 36)
    private String walletId;

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 30)
    private String eventType;

    @Column(length = 36)
    private String transactionId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String eventData;

    @Column(length = 36)
    private String counterpartyWalletId;

    @Column(nullable = false)
    private Instant occurredAt;
}
