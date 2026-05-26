package com.example.tem_on.event.domain.dto;

import com.example.tem_on.event.domain.entity.EventEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String status;

    public EventResponse(EventEntity event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.status = event.getStatus().name(); 
    }
}