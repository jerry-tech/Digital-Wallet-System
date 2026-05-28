package com.jerry.history.repository;

import com.jerry.history.model.TransactionEvents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionEventsRepository extends JpaRepository<TransactionEvents, String> {
}
