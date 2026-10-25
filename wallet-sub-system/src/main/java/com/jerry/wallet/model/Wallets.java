package com.jerry.wallet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing user wallets.
 * @author Jeremiah Udoh
 * @since 27th May 2026
 */
@Entity
@Getter
@Setter
public class Wallets {
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Version
    private BigInteger version = BigInteger.valueOf(0);

    @Column(updatable = false, insertable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(insertable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
