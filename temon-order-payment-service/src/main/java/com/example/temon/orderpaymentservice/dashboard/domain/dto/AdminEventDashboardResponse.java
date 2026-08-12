package com.example.temon.orderpaymentservice.dashboard.domain.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminEventDashboardResponse {

    private Long eventId;
    private String eventTitle;

    private long eventProductCount;
    private long totalSales;
    private long totalSoldQuantity;
}