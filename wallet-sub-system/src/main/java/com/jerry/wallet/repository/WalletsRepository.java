package com.jerry.wallet.repository;

import com.jerry.wallet.model.Wallets;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface WalletsRepository extends JpaRepository<Wallets, String> {

    @Query("SELECT w FROM Wallets w WHERE w.userId = :userId")
    List<Wallets> findWalletsByUserId(@Param("userId") String userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallets w WHERE w.id IN :walletIds ORDER BY w.id") //Lock both wallets (ORDER BY id to prevent deadlock)
    List<Wallets> findWalletsForUpdate(@Param("walletIds") List<String> walletIds);
}
