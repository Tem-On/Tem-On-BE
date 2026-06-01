package com.example.tem_on.global.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.tem_on.global.kafka.event.StockChangedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StockChangedConsumer {

    @KafkaListener(
            topics = "stock-changed",
            groupId = "stock-group"
    )
    public void consume(StockChangedEvent event) {

        log.info(
                "재고 변경 이벤트 수신: productId={}, remain={}, reserved={}, sold={}",
                event.getEventProductId(),
                event.getRemainingQuantity(),
                event.getReservedQuantity(),
                event.getSoldQuantity()
        );
    }
}
