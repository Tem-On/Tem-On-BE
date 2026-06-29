package com.example.temon.orderpaymentservice.global.client;

public record ProductResponse(
    Long id,
    String name,
    int price
) {}