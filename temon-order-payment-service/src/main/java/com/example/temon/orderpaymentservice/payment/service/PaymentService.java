package com.example.temon.orderpaymentservice.payment.service;

import com.example.temon.common.dto.event.PaymentCanceledEvent;
import com.example.temon.common.dto.event.PaymentCompletedEvent;
import com.example.temon.common.dto.event.PaymentFailedEvent;
import com.example.temon.orderpaymentservice.global.kafka.producer.KafkaEventProducer;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import com.example.temon.orderpaymentservice.payment.domain.dto.PaymentRequest;
import com.example.temon.orderpaymentservice.payment.domain.dto.PaymentResponse;
import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentEntity;
import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentStatus;
import com.example.temon.orderpaymentservice.payment.repository.PaymentRepository;
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
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional
    public PaymentResponse requestPayment(Long userId, PaymentRequest request) {

        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 주문만 결제 가능합니다.");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalArgumentException("결제 가능한 주문 상태가 아닙니다.");
        }

        paymentRepository.findByOrderId(order.getId())
                .ifPresent(payment -> {
                    throw new IllegalArgumentException("이미 결제 요청이 생성된 주문입니다.");
                });

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

        PaymentEntity payment = getPaymentEntity(paymentId);

        if (payment.getStatus() != PaymentStatus.READY) {
            throw new IllegalArgumentException("결제 대기 상태에서만 성공 처리할 수 있습니다.");
        }

        payment.success();
        payment.getOrder().changeStatus(OrderStatus.PAID);

        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(payment.getOrder().getId())
                .userId(payment.getOrder().getUserId())
                .totalAmount(payment.getAmount())
                .items(
                        payment.getOrder().getOrderItems()
                                .stream()
                                .map(item -> PaymentCompletedEvent.PaymentCompletedItem.builder()
                                        .eventProductId(item.getEventProductId())
                                        .quantity(item.getQuantity())
                                        .build()
                                )
                                .toList()
                )
                .build();

        kafkaEventProducer.publish("payment-completed", event);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse fail(Long paymentId) {

        PaymentEntity payment = getPaymentEntity(paymentId);

        if (payment.getStatus() != PaymentStatus.READY) {
            throw new IllegalArgumentException("결제 대기 상태에서만 실패 처리할 수 있습니다.");
        }

        payment.fail();
        payment.getOrder().changeStatus(OrderStatus.CANCELED);

        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(payment.getOrder().getId())
                .userId(payment.getOrder().getUserId())
                .items(
                        payment.getOrder().getOrderItems()
                                .stream()
                                .map(item -> PaymentFailedEvent.PaymentFailedItem.builder()
                                        .eventProductId(item.getEventProductId())
                                        .quantity(item.getQuantity())
                                        .build()
                                )
                                .toList()
                )
                .build();

        kafkaEventProducer.publish("payment-failed", event);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse cancel(Long paymentId) {

        PaymentEntity payment = getPaymentEntity(paymentId);

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 결제입니다.");
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new IllegalArgumentException("실패한 결제는 취소할 수 없습니다.");
        }

        PaymentStatus previousStatus = payment.getStatus();

        payment.cancel();
        payment.getOrder().changeStatus(OrderStatus.CANCELED);

        PaymentCanceledEvent event = PaymentCanceledEvent.builder()
                .orderId(payment.getOrder().getId())
                .userId(payment.getOrder().getUserId())
                .previousPaymentStatus(previousStatus.name())
                .items(
                        payment.getOrder().getOrderItems()
                                .stream()
                                .map(item -> PaymentCanceledEvent.PaymentCanceledItem.builder()
                                        .eventProductId(item.getEventProductId())
                                        .quantity(item.getQuantity())
                                        .build()
                                )
                                .toList()
                )
                .build();

        kafkaEventProducer.publish("payment-canceled", event);

        return PaymentResponse.from(payment);
    }

    public PaymentResponse getPayment(Long paymentId) {
        return PaymentResponse.from(getPaymentEntity(paymentId));
    }

    private PaymentEntity getPaymentEntity(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));
    }
}