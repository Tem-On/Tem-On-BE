package com.example.temon.commerceservice.event.service;

import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventResponse;
import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import com.example.temon.commerceservice.event.domain.entity.EventStatus;
import com.example.temon.commerceservice.event.repository.EventRepository;

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
    private final EventProductService eventProductService;

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllActiveEventsWithoutDeleted(EventStatus.DELETED)
                .stream()
                .map(event -> {
                    List<EventProductResponse> products =
                            eventProductService.getProductsByEventId(event.getId());

                    return new EventResponse(event, products);
                })
                .collect(Collectors.toList());
    }

    public EventResponse getEventDetail(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 이벤트입니다. id=" + eventId)
                );

        if (event.getStatus() == EventStatus.DELETED) {
            throw new IllegalArgumentException("존재하지 않는 이벤트입니다. id=" + eventId);
        }

        List<EventProductResponse> products =
                eventProductService.getProductsByEventId(eventId);

        return new EventResponse(event, products);
    }

    public List<EventResponse> getOpenEvents() {
        LocalDateTime now = LocalDateTime.now();

        return eventRepository.findActiveEvents(now, EventStatus.OPEN)
                .stream()
                .map(event -> {
                    List<EventProductResponse> products =
                            eventProductService.getProductsByEventId(event.getId());

                    return new EventResponse(event, products);
                })
                .collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();

        return eventRepository.findUpcomingEvents(now, EventStatus.UPCOMING)
                .stream()
                .map(event -> {
                    List<EventProductResponse> products =
                            eventProductService.getProductsByEventId(event.getId());

                    return new EventResponse(event, products);
                })
                .collect(Collectors.toList());
    }
}