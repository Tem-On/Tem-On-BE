package com.example.temon.commerceservice.event.service;

import com.example.temon.commerceservice.event.domain.dto.EventCreateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventResponse;
import com.example.temon.commerceservice.event.domain.dto.EventStatusUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventUpdateRequest;
import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import com.example.temon.commerceservice.event.domain.entity.EventStatus;
import com.example.temon.commerceservice.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import com.example.temon.common.dto.event.EventStatusChangedEvent;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void createEvent(EventCreateRequest request) {
        EventEntity event = EventEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(EventStatus.UPCOMING)
                .build();
        
        eventRepository.save(event);
    }

    public List<EventResponse> getEventList() {
        return eventRepository.findAll().stream()
                .map(EventResponse::new) 
                .collect(Collectors.toList());
    }

    public EventResponse getEventDetail(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + eventId));
        return new EventResponse(event);
    }

    @Transactional
    public void updateEvent(Long eventId, EventUpdateRequest request) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + eventId));
        
        event.updateEventInfo(
                request.getTitle(), 
                request.getDescription(), 
                request.getStartAt(), 
                request.getEndAt()
        );
    }

    @Transactional
    public void updateEventStatus(
            Long eventId,
            EventStatusUpdateRequest request
    ) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 이벤트입니다. ID: " + eventId
                        ));

        EventStatus newStatus =
                EventStatus.valueOf(
                        request.getStatus().toUpperCase()
                );

        event.updateStatus(newStatus);

        kafkaTemplate.send("event-status-topic", new EventStatusChangedEvent(event.getId(), newStatus.name()));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + eventId));
        
        event.delete(); 
    }
}