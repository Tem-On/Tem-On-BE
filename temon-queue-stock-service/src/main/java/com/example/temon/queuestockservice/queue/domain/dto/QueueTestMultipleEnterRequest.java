package com.example.temon.queuestockservice.queue.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QueueTestMultipleEnterRequest {

    private Long eventProductId;
    private Long startUserId;
    private Integer count;
}