package com.example.temon.orderpaymentservice.dashboard.domain.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardResponse {

    private long totalOrders;
    private long paidOrders;
    private long canceledOrders;
    private long totalSales;

    private long activeEvents;
    private long soldOutEventProducts;
    private long totalSoldQuantity;
}