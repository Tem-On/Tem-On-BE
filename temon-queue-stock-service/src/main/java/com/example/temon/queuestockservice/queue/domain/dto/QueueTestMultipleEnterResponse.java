package com.example.temon.queuestockservice.queue.domain.dto;

public record QueueTestMultipleEnterResponse(
        Long eventProductId,
        Long startUserId,
        Integer requestedCount,
        Long addedCount
) {
}