package com.example.temon.commerceservice.event.domain.dto;

public record EventProductValidationResponse(
        Long eventProductId,
        boolean exists,
        boolean queueAvailable,
        String eventStatus,
        String eventProductStatus
) {
}