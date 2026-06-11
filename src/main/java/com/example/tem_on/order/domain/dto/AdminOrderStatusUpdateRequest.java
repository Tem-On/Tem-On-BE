package com.example.tem_on.order.domain.dto;

import com.example.tem_on.order.domain.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderStatusUpdateRequest {

    @NotNull(message = "변경할 주문 상태는 필수입니다.")
    private OrderStatus status;
}