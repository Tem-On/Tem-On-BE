package com.example.temon.queuestockservice.global.kafka.consumer;

import com.example.temon.common.dto.event.PaymentCanceledEvent;
import com.example.temon.common.dto.event.PaymentCompletedEvent;
import com.example.temon.common.dto.event.PaymentFailedEvent;
import com.example.temon.queuestockservice.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStockEventConsumer {

    private final StockService stockService;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "stock-payment-completed-group"
    )
    public void consumePaymentCompleted(PaymentCompletedEvent event) {
        log.info("결제 완료 이벤트 수신: orderId={}, userId={}, totalAmount={}",
                event.getOrderId(),
                event.getUserId(),
                event.getTotalAmount());

        for (PaymentCompletedEvent.PaymentCompletedItem item : event.getItems()) {
            stockService.confirmStock(
                    item.getEventProductId(),
                    item.getQuantity()
            );
        }

        log.info("Kafka 기반 재고 확정 완료: orderId={}", event.getOrderId());
    }

    @KafkaListener(
            topics = "payment-failed",
            groupId = "stock-payment-failed-group"
    )
    public void consumePaymentFailed(PaymentFailedEvent event) {
        log.info("결제 실패 이벤트 수신: orderId={}, userId={}",
                event.getOrderId(),
                event.getUserId());

        for (PaymentFailedEvent.PaymentFailedItem item : event.getItems()) {
            stockService.releaseStock(
                    item.getEventProductId(),
                    item.getQuantity()
            );
        }

        log.info("Kafka 기반 선점 재고 복구 완료: orderId={}", event.getOrderId());
    }

    @KafkaListener(
            topics = "payment-canceled",
            groupId = "stock-payment-canceled-group"
    )
    public void consumePaymentCanceled(PaymentCanceledEvent event) {
        log.info("결제 취소 이벤트 수신: orderId={}, userId={}, previousStatus={}",
                event.getOrderId(),
                event.getUserId(),
                event.getPreviousPaymentStatus());

        for (PaymentCanceledEvent.PaymentCanceledItem item : event.getItems()) {
            if ("READY".equals(event.getPreviousPaymentStatus())) {
                stockService.releaseStock(
                        item.getEventProductId(),
                        item.getQuantity()
                );
            }

            if ("PAID".equals(event.getPreviousPaymentStatus())) {
                stockService.cancelSoldStock(
                        item.getEventProductId(),
                        item.getQuantity()
                );
            }
        }

        log.info("Kafka 기반 결제 취소 재고 처리 완료: orderId={}", event.getOrderId());
    }
}