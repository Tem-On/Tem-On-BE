package com.example.tem_on.global.kafka.event;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private Long orderId;
    private Long userId;

    private List<PaymentFailedItem> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentFailedItem {
        private Long eventProductId;
        private Integer quantity;
    }
}