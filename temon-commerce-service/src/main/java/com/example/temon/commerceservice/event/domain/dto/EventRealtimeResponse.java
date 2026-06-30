package com.example.temon.commerceservice.event.domain.dto;


import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import lombok.Getter;

@Getter
public class EventRealtimeResponse {

    private final Long eventId;
    private final String title;
    private final String status;

    public EventRealtimeResponse(EventEntity event) {
        this.eventId = event.getId();
        this.title = event.getTitle();
        this.status = event.getStatus().name();
    }
}