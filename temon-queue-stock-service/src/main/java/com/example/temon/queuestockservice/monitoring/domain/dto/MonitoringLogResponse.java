package com.example.temon.queuestockservice.monitoring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonitoringLogResponse {

    private String id;
    private String timestamp;
    private String level;
    private String source;
    private String message;
}