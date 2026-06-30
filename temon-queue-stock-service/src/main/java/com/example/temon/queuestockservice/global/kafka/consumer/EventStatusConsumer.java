package com.example.temon.queuestockservice.global.kafka.consumer;

import com.example.temon.common.dto.event.EventStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventStatusConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "event-status-topic",
            groupId = "queue-stock-group"
    )
    public void consume(EventStatusChangedEvent event) {
        messagingTemplate.convertAndSend("/topic/events", event);
    }
}