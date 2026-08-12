package com.example.temon.queuestockservice.monitoring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KafkaStatusResponse {

    private String status;
    private int brokerCount;
}