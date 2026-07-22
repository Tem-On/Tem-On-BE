package com.example.temon.queuestockservice.global.client;

import java.time.LocalDateTime;

public record EventProductClientResponse(
        Long id,
        Long eventId,
        String eventTitle,
        Long productId,
        String productName,
        String productImageUrl,
        Integer originalPrice,
        String categoryName,
        String productStatus,
        Integer eventPrice,
        Integer purchaseLimit,
        String eventProductStatus,
        LocalDateTime createdAt,
        Integer totalStock,
        Integer remainingStock,
        Integer soldCount
) {
}