package com.example.temon.queuestockservice.monitoring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebSocketStatusResponse {

    private String status;
    private int connectedUsers;
}