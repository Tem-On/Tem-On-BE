package com.example.temon.orderpaymentservice.global.client;

public record StockResponse(
        Long eventProductId,
        Integer quantity,
        Integer soldQuantity
) {}