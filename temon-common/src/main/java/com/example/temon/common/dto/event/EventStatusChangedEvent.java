package com.example.temon.common.dto.event;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusChangedEvent {
    private Long eventId;
    private String newStatus; 
}