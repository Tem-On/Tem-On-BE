package com.example.temon.common.dto.event;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    private Long orderId;
    private Long userId;
    private Integer totalAmount;

    private List<PaymentCompletedItem> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentCompletedItem {
        private Long eventProductId;
        private Integer quantity;
    }
}