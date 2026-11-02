package com.jerry.wallet.repository;

import com.jerry.wallet.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100BySentAtIsNullAndAttemptsLessThanOrderByIdAsc(int maxAttempts);

    @Modifying
    @Transactional
    @Query("delete from OutboxEvent e where e.sentAt is not null and e.sentAt < :cutoff")
    int purgeSentBefore(@Param("cutoff") Instant cutoff);
}
