package com.example.temon.commerceservice.event.domain.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventUpdateRequest {

    private String title;
    private String description;
    private LocalDateTime startAt; 
    private LocalDateTime endAt; 
}