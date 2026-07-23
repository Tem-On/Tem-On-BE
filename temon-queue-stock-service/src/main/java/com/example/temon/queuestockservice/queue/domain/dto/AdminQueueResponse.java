package com.example.temon.queuestockservice.queue.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminQueueResponse {

    private Long eventProductId;
    private Long eventId;

    private String eventTitle;
    private String productName;

    private String gateStatus;
    private Long waitingCount;
    private Long enteredCount;
}