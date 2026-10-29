package com.jerry.wallet.listeners;

import com.google.gson.Gson;
import com.jerry.wallet.config.KafkaTopicProperties;
import com.jerry.wallet.dto.WalletEventDto;
import com.jerry.wallet.model.OutboxEvent;
import com.jerry.wallet.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WalletEventOutboxWriter {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTopicProperties topics;
    private final Gson gson;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void write(WalletEventDto event) {
        OutboxEvent row = new OutboxEvent();
        row.setTopic(topicFor(event));
        row.setMessageKey(event.walletId());
        row.setPayload(gson.toJson(event));
        outboxEventRepository.save(row);    // commits or rolls back together with the wallet update
    }

    private String topicFor(WalletEventDto event) {
        return switch (event.eventType()) {
            case WALLET_CREATED -> topics.getWalletCreationTopic();
            case WALLET_FUNDED -> topics.getWalletFundingTopic();
            case TRANSFER_COMPLETED, TRANSFER_FAILED -> topics.getWalletTransferTopic();
        };
    }
}