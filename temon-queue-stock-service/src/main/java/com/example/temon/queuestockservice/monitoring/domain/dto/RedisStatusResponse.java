package com.example.temon.queuestockservice.monitoring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedisStatusResponse {

    private String status;
    private long connectedClients;
}