package com.example.temon.orderpaymentservice.order.domain.dto;

import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderStatusUpdateRequest {

    @NotNull(message = "변경할 주문 상태는 필수입니다.")
    private OrderStatus status;
}