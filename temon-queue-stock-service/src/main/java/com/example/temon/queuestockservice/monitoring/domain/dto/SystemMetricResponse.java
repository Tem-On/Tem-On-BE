package com.example.temon.queuestockservice.monitoring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemMetricResponse {

    private String label;
    private String value;
    private String unit;
    private String status;
}