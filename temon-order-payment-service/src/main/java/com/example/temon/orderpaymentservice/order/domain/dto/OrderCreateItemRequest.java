package com.example.temon.orderpaymentservice.order.domain.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderCreateItemRequest {

    private Long eventProductId;
    private int quantity;
}