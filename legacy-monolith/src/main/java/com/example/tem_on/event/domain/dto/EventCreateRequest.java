package com.example.tem_on.event.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventCreateRequest {
    
    private String title;
    private String description;
    private LocalDateTime startAt; 
    private LocalDateTime endAt; 
}