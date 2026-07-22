package com.example.temon.commerceservice.event.service;

import com.example.temon.common.dto.event.EventStatusChangedEvent;
import com.example.temon.commerceservice.event.domain.dto.EventCreateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventResponse;
import com.example.temon.commerceservice.event.domain.dto.EventStatusUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventUpdateRequest;
import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import com.example.temon.commerceservice.event.domain.entity.EventStatus;
import com.example.temon.commerceservice.event.repository.EventRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {

    private final EventRepository eventRepository;
    private final EventProductService eventProductService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public EventResponse createEvent(
            EventCreateRequest request
    ) {
        validateTitle(request.getTitle());

        validateEventPeriod(
                request.getStartAt(),
                request.getEndAt()
        );

        EventEntity event = EventEntity.builder()
                .title(request.getTitle().trim())
                .description(
                        request.getDescription() == null
                                ? ""
                                : request.getDescription().trim()
                )
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(EventStatus.UPCOMING)
                .build();

        EventEntity savedEvent =
                eventRepository.save(event);

        return new EventResponse(savedEvent);
    }

    public List<EventResponse> getEventList() {
        return eventRepository
                .findByStatusNotOrderByCreatedAtDesc(
                        EventStatus.DELETED
                )
                .stream()
                .map(this::createEventResponse)
                .toList();
    }

    public EventResponse getEventDetail(Long eventId) {
        EventEntity event = findEvent(eventId);

        return createEventResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(
            Long eventId,
            EventUpdateRequest request
    ) {
        validateTitle(request.getTitle());

        validateEventPeriod(
                request.getStartAt(),
                request.getEndAt()
        );

        EventEntity event = findEvent(eventId);

        event.updateEventInfo(
                request.getTitle().trim(),
                request.getDescription() == null
                        ? ""
                        : request.getDescription().trim(),
                request.getStartAt(),
                request.getEndAt()
        );

        eventRepository.flush();

        return createEventResponse(event);
    }

    @Transactional
    public EventResponse updateEventStatus(
            Long eventId,
            EventStatusUpdateRequest request
    ) {
        EventEntity event = findEvent(eventId);

        EventStatus newStatus =
                parseEventStatus(request.getStatus());

        if (newStatus == EventStatus.DELETED) {
            throw new IllegalArgumentException(
                    "상태 변경 API에서는 DELETED 상태를 사용할 수 없습니다."
            );
        }

        event.updateStatus(newStatus);

        eventRepository.flush();

        kafkaTemplate.send(
                "event-status-topic",
                new EventStatusChangedEvent(
                        event.getId(),
                        newStatus.name()
                )
        );

        return createEventResponse(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        EventEntity event = findEvent(eventId);

        event.delete();

        eventRepository.flush();

        kafkaTemplate.send(
                "event-status-topic",
                new EventStatusChangedEvent(
                        event.getId(),
                        EventStatus.DELETED.name()
                )
        );
    }

    private EventResponse createEventResponse(
            EventEntity event
    ) {
        List<EventProductResponse> products =
                eventProductService.getProductsByEventId(
                        event.getId()
                );

        return new EventResponse(
                event,
                products
        );
    }

    private EventEntity findEvent(Long eventId) {
        EventEntity event =
                eventRepository.findById(eventId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 이벤트입니다. ID: "
                                                + eventId
                                )
                        );

        if (event.getStatus() == EventStatus.DELETED) {
            throw new IllegalArgumentException(
                    "삭제된 이벤트입니다. ID: "
                            + eventId
            );
        }

        return event;
    }

    private EventStatus parseEventStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "이벤트 상태를 입력해주세요."
            );
        }

        try {
            return EventStatus.valueOf(
                    status.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "올바르지 않은 이벤트 상태입니다: "
                            + status
            );
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "이벤트명을 입력해주세요."
            );
        }

        if (title.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "이벤트명은 100자 이하로 입력해주세요."
            );
        }
    }

    private void validateEventPeriod(
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        if (startAt == null) {
            throw new IllegalArgumentException(
                    "이벤트 시작일을 입력해주세요."
            );
        }

        if (endAt == null) {
            throw new IllegalArgumentException(
                    "이벤트 종료일을 입력해주세요."
            );
        }

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException(
                    "이벤트 종료일은 시작일보다 늦어야 합니다."
            );
        }
    }
}