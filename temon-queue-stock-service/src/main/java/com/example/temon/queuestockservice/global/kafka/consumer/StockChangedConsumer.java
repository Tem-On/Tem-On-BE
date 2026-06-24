package com.example.temon.queuestockservice.global.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.temon.common.dto.event.StockChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockChangedConsumer {

    private final SimpMessagingTemplate messagingTemplate;

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

        messagingTemplate.convertAndSend(
                "/topic/stocks/" + event.getEventProductId(),
                event
        );
    }
}