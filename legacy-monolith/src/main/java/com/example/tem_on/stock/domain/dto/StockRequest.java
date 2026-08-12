package com.example.tem_on.stock.domain.dto;


import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockRequest {
    private Long eventProductId; 
    private int quantity;       
}