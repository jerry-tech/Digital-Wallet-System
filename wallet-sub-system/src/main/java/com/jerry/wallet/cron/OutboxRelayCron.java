package com.jerry.wallet.cron;

import com.jerry.wallet.model.OutboxEvent;
import com.jerry.wallet.repository.OutboxEventRepository;
import com.jerry.wallet.service.kafka.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayCron {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaPublisherService kafkaPublisherService;

    @Value("${outbox.max-attempts:5}")
    private int maxAttempts;

    @Value("${outbox.retention-days:7}")
    private long retentionDays;

    @Scheduled(fixedDelay = 1000)
    public void relay() {
        for (OutboxEvent event : outboxEventRepository.findTop100BySentAtIsNullAndAttemptsLessThanOrderByIdAsc(maxAttempts)) {
            try {
                kafkaPublisherService.sendMessageWithCallback(event.getMessageKey(), event.getPayload(), event.getTopic())
                        .get(5, TimeUnit.SECONDS);
                event.setSentAt(Instant.now());
                outboxEventRepository.save(event);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();// shutting down: stop without counting an attempt
                return;
            } catch (Exception ex) {
                if (isBrokerUnavailable(ex)) {
                    log.warn("Kafka unavailable, will retry outbox id={}", event.getId());
                    return;
                }
                event.setAttempts(event.getAttempts() + 1);
                outboxEventRepository.save(event);
                if (event.getAttempts() >= maxAttempts) {
                    log.error("OUTBOX_PARKED id={} topic={} attempts={} payload={}", event.getId(), event.getTopic(), event.getAttempts(), event.getPayload(), ex);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void purge() {
        int removed = outboxEventRepository.purgeSentBefore(Instant.now().minus(retentionDays, ChronoUnit.DAYS));
        log.info("Outbox purge removed {} rows older than {} days", removed, retentionDays);
    }

    private boolean isBrokerUnavailable(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof java.util.concurrent.TimeoutException
                    || t instanceof org.apache.kafka.common.errors.RetriableException) {
                return true;
            }
        }
        return false;
    }
}