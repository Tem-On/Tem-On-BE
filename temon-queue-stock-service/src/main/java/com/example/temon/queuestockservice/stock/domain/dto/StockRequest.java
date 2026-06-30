package com.example.temon.queuestockservice.stock.domain.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockRequest {
    private Long eventProductId; 
    private int quantity;       
}