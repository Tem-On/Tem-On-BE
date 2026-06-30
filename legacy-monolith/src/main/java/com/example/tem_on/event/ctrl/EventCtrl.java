package com.example.tem_on.event.ctrl;


import com.example.tem_on.event.domain.dto.EventResponse;
import com.example.tem_on.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "이벤트 관련 API")
public class EventCtrl {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "이벤트 전체 목록 조회")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/open")
    @Operation(summary = "진행 중인 이벤트 조회")
    public ResponseEntity<List<EventResponse>> getOpenEvents() {
        return ResponseEntity.ok(eventService.getOpenEvents());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "예정된 이벤트 조회")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    public ResponseEntity<EventResponse> getEventDetail(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventDetail(eventId));
    }
}