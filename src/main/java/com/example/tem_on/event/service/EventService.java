package com.example.tem_on.event.service;

import com.example.tem_on.event.domain.dto.EventResponse;
import com.example.tem_on.event.domain.entity.EventEntity;
import com.example.tem_on.event.domain.entity.EventStatus;
import com.example.tem_on.event.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    public EventResponse getEventDetail(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. id=" + eventId));
        return new EventResponse(event);
    }

    public List<EventResponse> getOpenEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findActiveEvents(now, EventStatus.OPEN).stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingEvents(now, EventStatus.UPCOMING).stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }
}