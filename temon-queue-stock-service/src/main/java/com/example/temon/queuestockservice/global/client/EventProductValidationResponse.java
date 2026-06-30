package com.example.temon.queuestockservice.global.client;

public record EventProductValidationResponse(
        Long eventProductId,
        boolean exists,
        boolean queueAvailable,
        String eventStatus,
        String eventProductStatus
) {
}