package com.example.tem_on.event.service;

import com.example.tem_on.event.domain.dto.EventCreateRequest;
import com.example.tem_on.event.domain.dto.EventResponse;
import com.example.tem_on.event.domain.dto.EventStatusUpdateRequest;
import com.example.tem_on.event.domain.dto.EventUpdateRequest;
import com.example.tem_on.event.domain.entity.EventEntity;
import com.example.tem_on.event.domain.entity.EventStatus;
import com.example.tem_on.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {

    private final EventRepository eventRepository;

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
    public void updateEventStatus(Long eventId, EventStatusUpdateRequest request) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + eventId));
        
        EventStatus newStatus = EventStatus.valueOf(request.getStatus().toUpperCase());
        event.updateStatus(newStatus);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + eventId));
        
        event.delete(); 
    }
}