package com.jerry.history.serviceImpl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerry.history.pojo.WalletEventPojo;
import com.jerry.history.service.TransactionEventService;
import com.jerry.history.service.kafka.KafkaListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerServiceImpl implements KafkaListenerService {

    private final ObjectMapper objectMapper;
    private final TransactionEventService transactionEventService;

    @KafkaListener(topics = {"${kafka.topics.wallet-creation-topic}", "${kafka.topics.wallet-funding-topic}", "${kafka.topics.wallet-transfer-topic}"})
    public void onWalletEvent(String payload) throws JsonProcessingException {
        WalletEventPojo event = objectMapper.readValue(payload, WalletEventPojo.class);
        if (event.eventId() == null || event.eventId().isBlank()) {
            throw new IllegalArgumentException("Event without eventId");
        }
        transactionEventService.logWalletTransactionEventHistory(event, payload);
    }
}
