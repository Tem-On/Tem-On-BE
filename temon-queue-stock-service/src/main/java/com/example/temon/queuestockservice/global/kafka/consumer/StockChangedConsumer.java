package com.example.temon.queuestockservice.global.kafka.consumer;

import com.example.temon.common.dto.event.StockChangedEvent;
import com.example.temon.queuestockservice.stock.metric.StockMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockChangedConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final StockMetrics stockMetrics;

    @KafkaListener(
            topics = "stock-changed",
            groupId = "stock-group"
    )
    public void consume(StockChangedEvent event) {

        if (event == null
                || event.getEventProductId() == null) {

            log.warn(
                    "잘못된 재고 변경 이벤트를 수신했습니다."
            );

            return;
        }

        log.info(
                "재고 변경 이벤트 수신: "
                        + "eventProductId={}, "
                        + "remaining={}, "
                        + "reserved={}, "
                        + "sold={}",
                event.getEventProductId(),
                event.getRemainingQuantity(),
                event.getReservedQuantity(),
                event.getSoldQuantity()
        );

        /*
         * Kafka 이벤트 내용으로 Prometheus Gauge를 갱신한다.
         */
        stockMetrics.update(
                event.getEventProductId(),
                event.getRemainingQuantity(),
                event.getReservedQuantity(),
                event.getSoldQuantity()
        );

        /*
         * 프론트엔드 WebSocket 실시간 재고 알림.
         */
        messagingTemplate.convertAndSend(
                "/topic/stocks/"
                        + event.getEventProductId(),
                event
        );
    }
}