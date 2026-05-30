package com.example.tem_on.payment.service;

import com.example.tem_on.order.domain.entity.OrderEntity;
import com.example.tem_on.order.domain.entity.OrderStatus;
import com.example.tem_on.order.repository.OrderRepository;
import com.example.tem_on.payment.domain.dto.PaymentRequest;
import com.example.tem_on.payment.domain.dto.PaymentResponse;
import com.example.tem_on.payment.domain.entity.PaymentEntity;
import com.example.tem_on.payment.domain.entity.PaymentStatus;
import com.example.tem_on.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse requestPayment(Long userId,
                                          PaymentRequest request) {

        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 주문만 결제 가능합니다.");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .paymentNumber(UUID.randomUUID().toString())
                .order(order)
                .amount(order.getTotalAmount())
                .method(request.getMethod())
                .status(PaymentStatus.READY)
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse success(Long paymentId) {

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        payment.success();

        payment.getOrder().changeStatus(OrderStatus.PAID);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse fail(Long paymentId) {

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        payment.fail();

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse cancel(Long paymentId) {

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        payment.cancel();

        payment.getOrder().changeStatus(OrderStatus.CANCELED);

        return PaymentResponse.from(payment);
    }

    public PaymentResponse getPayment(Long paymentId) {

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        return PaymentResponse.from(payment);
    }
}