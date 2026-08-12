package com.example.tem_on.monitoring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MonitoringRealtimeResponse {

    private KafkaStatusResponse kafka;
    private RedisStatusResponse redis;
    private WebSocketStatusResponse websocket;
    private LocalDateTime checkedAt;
}