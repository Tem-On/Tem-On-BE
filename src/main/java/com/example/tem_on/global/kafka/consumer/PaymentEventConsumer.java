package com.example.tem_on.global.kafka.consumer;

import com.example.tem_on.global.kafka.event.PaymentCompletedEvent;
import com.example.tem_on.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final StockService stockService;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "temon-payment-group"
    )
    public void consume(PaymentCompletedEvent event) {
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
}