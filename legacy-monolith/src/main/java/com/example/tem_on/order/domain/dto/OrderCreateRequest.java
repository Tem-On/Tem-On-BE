package com.example.tem_on.order.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    private List<OrderCreateItemRequest> items;
}