package com.example.tem_on.dashboard.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminSalesDashboardResponse {

    private long totalSales;
    private long paidOrders;
    private long averageOrderAmount;
}