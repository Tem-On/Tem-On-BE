package com.example.tem_on.order.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminOrderEventProductStatisticsResponse {

    private Long eventProductId;
    private String productName;
    private long orderCount;
    private long soldQuantity;
    private long salesAmount;

    public AdminOrderEventProductStatisticsResponse(
            Long eventProductId,
            String productName,
            long orderCount,
            long soldQuantity,
            long salesAmount
    ) {
        this.eventProductId = eventProductId;
        this.productName = productName;
        this.orderCount = orderCount;
        this.soldQuantity = soldQuantity;
        this.salesAmount = salesAmount;
    }
}