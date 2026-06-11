package com.example.tem_on.order.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminOrderStatisticsResponse {

    private long totalOrderCount;
    private long createdOrderCount;
    private long paidOrderCount;
    private long canceledOrderCount;
    private long totalSales;
}