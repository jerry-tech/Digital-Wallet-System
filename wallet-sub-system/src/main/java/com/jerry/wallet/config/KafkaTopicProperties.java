package com.jerry.wallet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "kafka.topics", ignoreInvalidFields = true)
@Getter
@Setter
public class KafkaTopicProperties {
    private String walletCreationTopic;
}
