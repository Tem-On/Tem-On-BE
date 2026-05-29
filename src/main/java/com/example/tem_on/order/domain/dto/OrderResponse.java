package com.example.tem_on.order.domain.dto;

import com.example.tem_on.order.domain.entity.OrderEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private String orderNumber;
    private int totalAmount;
    private String status;
    private LocalDateTime orderedAt;
    private LocalDateTime canceledAt;
    private List<OrderItemResponse> items;

    public static OrderResponse from(OrderEntity order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderedAt(order.getOrderedAt())
                .canceledAt(order.getCanceledAt())
                .items(
                        order.getOrderItems().stream()
                                .map(OrderItemResponse::from)
                                .toList()
                )
                .build();
    }
}