package com.example.temon.orderpaymentservice.order.domain.dto;

import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminOrderResponse {

    private Long orderId;
    private Long userId;
    private String orderNumber;
    private int totalAmount;
    private String status;
    private String productName;
    private LocalDateTime orderedAt;
    private LocalDateTime canceledAt;

    public static AdminOrderResponse from(OrderEntity order, String productName) {
        return AdminOrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .productName(productName)
                .orderedAt(order.getOrderedAt())
                .canceledAt(order.getCanceledAt())
                .build();
    }
}