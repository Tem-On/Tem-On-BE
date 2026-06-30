package com.example.temon.queuestockservice.queue.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminQueueResponse {

    private Long eventProductId;
    private String status;
    private Long waitingUsers;
}