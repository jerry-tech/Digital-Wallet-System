package com.jerry.history.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EntityScan("com.jerry.history.model")
public class CustomConfiguration {

    /*
    * Poison messages and failed events:
    * When eventId is blank, the listener neither acknowledges nor throws, so the message is silently dropped.
    * Malformed JSON throws, is retried a few times, then dropped with only a log line.
    * A retry policy and a dead-letter topic so bad events can be inspected
    */
    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template),
                new FixedBackOff(1000L, 3));
        handler.addNotRetryableExceptions(JsonProcessingException.class, IllegalArgumentException.class);
        return handler;
    }
}
