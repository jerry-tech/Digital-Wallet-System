package com.jerry.wallet.serviceImpl.kafka;


import com.jerry.wallet.service.kafka.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class KafkaPublisherServiceImpl implements KafkaPublisherService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String message, String topicName) {
        kafkaTemplate.send(topicName, message);
    }

    @Override
    public CompletableFuture<SendResult<String, String>> sendMessageWithCallback(String message, String topicName) {
        return kafkaTemplate.send(topicName, message);
    }
    @Override
    public CompletableFuture<SendResult<String, String>> sendMessageWithCallback(String key, String message, String topicName) {
        return kafkaTemplate.send(topicName, key, message);
    }
}
