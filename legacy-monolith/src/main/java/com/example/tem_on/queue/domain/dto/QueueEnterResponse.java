package com.example.tem_on.queue.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueEnterResponse {

    private Long eventProductId;
    private Long userId;
    private Long rank;
    private String status;
}