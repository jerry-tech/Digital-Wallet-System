package com.jerry.history.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Entity representing transaction events for auditing and tracking purposes.
 * @author Jeremiah Udoh
 * @since 27th May 2026
 */
@Entity
@Getter
@Setter
public class TransactionEvents {
    @Id
    private String id = UUID.randomUUID().toString();

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

    @Column(updatable = false, insertable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Lob
    @Column(columnDefinition = "jsonb")
    private String eventData;
}
