package com.jerry.wallet.repository;

import com.jerry.wallet.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop100ByAttemptsLessThanOrderByIdAsc(int maxAttempt);
}
