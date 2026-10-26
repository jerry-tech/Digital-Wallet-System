package com.jerry.wallet.serviceImpl;

import com.google.gson.Gson;
import com.jerry.wallet.config.KafkaTopicProperties;
import com.jerry.wallet.dto.WalletEventDto;
import com.jerry.wallet.service.kafka.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {

    private final KafkaPublisherService kafkaPublisherService;
    private final KafkaTopicProperties topics;
    private final Gson gson;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onWalletEvent(WalletEventDto event) {
        String topic = switch (event.eventType()) {
            case WALLET_CREATED -> topics.getWalletCreationTopic();
            case WALLET_FUNDED -> topics.getWalletFundingTopic();
            case TRANSFER_COMPLETED, TRANSFER_FAILED -> topics.getWalletTransferTopic();
        };

        kafkaPublisherService
                .sendMessageWithCallback(event.walletId(), gson.toJson(event), topic)   // key = walletId (ordering)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka publish failed eventId={} type={}", event.eventId(), event.eventType(), ex);
                    }
                });
    }
}