package com.example.temon.orderpaymentservice.order.domain.dto;


import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminOrderNotification {

    private Long orderId;
    private Long userId;
    private String orderNumber;
    private int totalAmount;
    private String status;
    private LocalDateTime orderedAt;

    public static AdminOrderNotification from(OrderEntity order) {
        return AdminOrderNotification.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderedAt(order.getOrderedAt())
                .build();
    }
}