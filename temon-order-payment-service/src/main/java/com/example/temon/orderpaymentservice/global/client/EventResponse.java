package com.example.temon.orderpaymentservice.global.client;

public record EventResponse(
        Long id,
        String title,
        String status 
) {}