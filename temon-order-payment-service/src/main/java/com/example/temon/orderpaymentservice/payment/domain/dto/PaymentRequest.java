package com.example.temon.orderpaymentservice.payment.domain.dto;

import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentMethod;
import lombok.Getter;

@Getter
public class PaymentRequest {

    private Long orderId;

    private PaymentMethod method;
}