package com.jerry.wallet.cron;

import com.jerry.wallet.model.OutboxEvent;
import com.jerry.wallet.repository.OutboxEventRepository;
import com.jerry.wallet.service.kafka.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayCron {

    private final OutboxEventRepository outbox;
    private final KafkaPublisherService kafka;

    @Value("${outbox.max-attempts:5}")
    private int maxAttempts;

    @Scheduled(fixedDelay = 1000)
    public void relay() {
        for (OutboxEvent event : outbox.findTop100ByAttemptsLessThanOrderByIdAsc(maxAttempts)) {
            try {
                kafka.sendMessageWithCallback(event.getMessageKey(), event.getPayload(), event.getTopic()).get(5, TimeUnit.SECONDS);
                outbox.delete(event);
            } catch (Exception ex) {
                if (isBrokerUnavailable(ex)) {
                    log.warn("Kafka unavailable, will retry outbox id={}", event.getId());   // does not count
                    return;
                }
                event.setAttempts(event.getAttempts() + 1);
                outbox.save(event);
                if (event.getAttempts() >= maxAttempts) {
                    log.error("OUTBOX_PARKED id={} topic={} attempts={} payload={}", event.getId(), event.getTopic(), event.getAttempts(), event.getPayload(), ex);
                }
            }
        }
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