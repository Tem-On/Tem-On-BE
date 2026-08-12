package com.example.tem_on.queue.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 대기열 test용
@Getter
@NoArgsConstructor
public class QueueTestEnterRequest {

    private Long eventProductId;

    private Long userId;
}