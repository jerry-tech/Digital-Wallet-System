package com.jerry.wallet.repository;

import com.jerry.wallet.model.Wallets;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletsRepository extends JpaRepository<Wallets, String> {
    List<Wallets> findWalletsByUserId(String userId, Pageable pageable);

}
