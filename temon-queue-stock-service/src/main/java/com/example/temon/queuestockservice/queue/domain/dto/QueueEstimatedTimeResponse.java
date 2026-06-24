package com.example.temon.queuestockservice.queue.domain.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueEstimatedTimeResponse {

    private Long estimatedSeconds;
}