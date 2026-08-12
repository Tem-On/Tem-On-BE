package com.example.temon.orderpaymentservice.global.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    private Long eventProductId;
    private int quantity;
}