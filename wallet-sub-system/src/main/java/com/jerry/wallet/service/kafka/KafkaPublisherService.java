package com.jerry.wallet.service.kafka;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface KafkaPublisherService {
    void sendMessage(String message, String topicName);

    CompletableFuture<SendResult<String, String>> sendMessageWithCallback(String message, String topicName);

    CompletableFuture<SendResult<String, String>> sendMessageWithCallback(String key, String message, String topicName);
}
