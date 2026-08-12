package com.example.temon.queuestockservice.queue.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueueRealtimeResponse {

    private Long eventProductId;
    private Long currentUsers;
    private String type;
    private String message;
}