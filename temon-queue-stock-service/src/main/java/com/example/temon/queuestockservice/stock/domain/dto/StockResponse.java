package com.example.temon.queuestockservice.stock.domain.dto;

import com.example.temon.queuestockservice.global.client.EventProductClientResponse;
import com.example.temon.queuestockservice.stock.domain.entity.StockEntity;
import lombok.Getter;

@Getter
public class StockResponse {
    private final Long id;
    private final Long eventProductId;
    private final String eventTitle;     
    private final String productName;
    private final int totalQuantity;
    private final int remainingQuantity;
    private final int reservedQuantity;
    private final int soldQuantity;

    public StockResponse(StockEntity entity) {
        this(entity, null); 
    }

    public StockResponse(StockEntity entity, EventProductClientResponse product) { 
        this.id = entity.getId();
        this.eventProductId = entity.getEventProductId();
        this.totalQuantity = entity.getTotalQuantity();
        this.remainingQuantity = entity.getRemainingQuantity();
        this.reservedQuantity = entity.getReservedQuantity();
        this.soldQuantity = entity.getSoldQuantity();

        if (product != null) {
            this.eventTitle = product.eventTitle();
            this.productName = product.productName();
        } else {
            this.eventTitle = "-";
            this.productName = "-";
        }
    }
}