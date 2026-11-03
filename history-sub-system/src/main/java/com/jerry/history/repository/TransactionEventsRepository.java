package com.jerry.history.repository;

import com.jerry.history.model.TransactionEvents;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionEventsRepository extends JpaRepository<TransactionEvents, String> {

    @Query("SELECT w FROM TransactionEvents w WHERE w.walletId = :walletId")
    List<TransactionEvents> findTransactionEventsByWalletId(@Param("walletId") String walletId, Pageable pageable);

    List<TransactionEvents> findTransactionEventsByUserId(String userId, Pageable pageable);
}
