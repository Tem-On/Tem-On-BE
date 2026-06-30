package com.example.temon.orderpaymentservice.order.domain.dto;

import com.example.temon.orderpaymentservice.order.domain.entity.OrderItemEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {

    private Long orderItemId;
    private Long eventProductId;
    private int quantity;
    private int orderPrice;
    private int totalPrice;
    private String productName;

    public static OrderItemResponse from(OrderItemEntity item) {
        return OrderItemResponse.builder()
                .orderItemId(item.getId())
                .eventProductId(item.getEventProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .orderPrice(item.getOrderPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}