package com.example.temon.commerceservice.event.domain.dto;


import lombok.Getter;
import java.time.LocalDateTime;
import com.example.temon.commerceservice.event.domain.entity.EventEntity;

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