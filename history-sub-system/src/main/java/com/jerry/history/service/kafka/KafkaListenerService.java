package com.jerry.history.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface KafkaListenerService {
    @KafkaHandler
    void onWalletEvent(String payload) throws JsonProcessingException;
}
