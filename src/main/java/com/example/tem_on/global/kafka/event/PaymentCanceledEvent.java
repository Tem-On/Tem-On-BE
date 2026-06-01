package com.example.tem_on.global.kafka.event;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCanceledEvent {

    private Long orderId;
    private Long userId;
    private String previousPaymentStatus;

    private List<PaymentCanceledItem> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentCanceledItem {
        private Long eventProductId;
        private Integer quantity;
    }
}