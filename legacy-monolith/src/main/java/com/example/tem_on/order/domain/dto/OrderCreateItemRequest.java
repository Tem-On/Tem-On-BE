package com.example.tem_on.order.domain.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderCreateItemRequest {

    private Long eventProductId;
    private int quantity;
}