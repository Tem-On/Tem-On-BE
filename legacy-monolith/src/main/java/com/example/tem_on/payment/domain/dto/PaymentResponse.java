package com.example.tem_on.payment.domain.dto;

import com.example.tem_on.payment.domain.entity.PaymentEntity;
import com.example.tem_on.payment.domain.entity.PaymentMethod;
import com.example.tem_on.payment.domain.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private Long paymentId;

    private String paymentNumber;

    private Long orderId;

    private Integer amount;

    private PaymentMethod method;

    private PaymentStatus status;

    public static PaymentResponse from(PaymentEntity payment) {

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .build();
    }
}