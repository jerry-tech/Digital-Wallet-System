package com.jerry.wallet.repository;

import com.jerry.wallet.model.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionsRepository extends JpaRepository<WalletTransactions, String> {
}
