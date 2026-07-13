package com.example.temon.commerceservice.event.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockInfoResponse {
    private Long eventProductId;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private Integer soldQuantity; 
}