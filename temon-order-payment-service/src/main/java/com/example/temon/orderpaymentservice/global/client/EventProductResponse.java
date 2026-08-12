package com.example.temon.orderpaymentservice.global.client;

public record EventProductResponse(
    Long id,
    Long productId,
    int eventPrice,
    int eventStock
) {}