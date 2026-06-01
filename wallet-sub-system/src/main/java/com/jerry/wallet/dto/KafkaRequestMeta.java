package com.jerry.wallet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KafkaRequestMeta {
    private String trackingId;
    private LocalDateTime requestTimestamp;
}
