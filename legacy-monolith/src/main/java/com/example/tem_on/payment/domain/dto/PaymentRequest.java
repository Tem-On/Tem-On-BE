package com.example.tem_on.payment.domain.dto;

import com.example.tem_on.payment.domain.entity.PaymentMethod;
import lombok.Getter;

@Getter
public class PaymentRequest {

    private Long orderId;

    private PaymentMethod method;
}