package com.example.tem_on.queue.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueRealtimeResponse {

    private Long eventProductId;
    private Long currentUsers;
    private String message;
}