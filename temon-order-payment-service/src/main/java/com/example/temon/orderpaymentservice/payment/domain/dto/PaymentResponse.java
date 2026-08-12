package com.example.temon.orderpaymentservice.payment.domain.dto;

import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentEntity;
import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentMethod;
import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentStatus;
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