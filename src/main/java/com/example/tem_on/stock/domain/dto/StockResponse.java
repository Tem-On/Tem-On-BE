package com.example.tem_on.stock.domain.dto;

import com.example.tem_on.stock.domain.entity.StockEntity;
import lombok.Getter;

@Getter
public class StockResponse {
    private final Long id;
    private final Long eventProductId;
    private final int totalQuantity;
    private final int remainingQuantity;
    private final int reservedQuantity;
    private final int soldQuantity;

    public StockResponse(StockEntity entity) {
        this.id = entity.getId();
        this.eventProductId = entity.getEventProductId();
        this.totalQuantity = entity.getTotalQuantity();
        this.remainingQuantity = entity.getRemainingQuantity();
        this.reservedQuantity = entity.getReservedQuantity();
        this.soldQuantity = entity.getSoldQuantity();
    }
}