package com.example.temon.commerceservice.event.ctrl;

import com.example.temon.commerceservice.event.domain.dto.EventCreateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventResponse;
import com.example.temon.commerceservice.event.domain.dto.EventStatusUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventUpdateRequest;
import com.example.temon.commerceservice.event.service.AdminEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Admin Event",
        description = "관리자 - 이벤트 관리 API"
)
@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventCtrl {

    private final AdminEventService adminEventService;

    @PostMapping
    @Operation(
            summary = "이벤트 생성",
            description = "새 이벤트를 생성합니다."
    )
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody EventCreateRequest request
    ) {
        EventResponse response =
                adminEventService.createEvent(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "이벤트 목록 조회",
            description = "삭제되지 않은 전체 이벤트를 조회합니다."
    )
    public ResponseEntity<List<EventResponse>> getEventList() {
        List<EventResponse> events =
                adminEventService.getEventList();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    @Operation(
            summary = "이벤트 상세 조회",
            description = "특정 이벤트의 상세 정보를 조회합니다."
    )
    public ResponseEntity<EventResponse> getEventDetail(
            @PathVariable Long eventId
    ) {
        EventResponse event =
                adminEventService.getEventDetail(eventId);

        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}")
    @Operation(
            summary = "이벤트 정보 수정",
            description = "이벤트명, 설명, 시작일, 종료일을 수정합니다."
    )
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequest request
    ) {
        EventResponse response =
                adminEventService.updateEvent(
                        eventId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{eventId}/status")
    @Operation(
            summary = "이벤트 상태 변경",
            description = "이벤트 상태를 변경합니다."
    )
    public ResponseEntity<EventResponse> updateEventStatus(
            @PathVariable Long eventId,
            @RequestBody EventStatusUpdateRequest request
    ) {
        EventResponse response =
                adminEventService.updateEventStatus(
                        eventId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    @Operation(
            summary = "이벤트 삭제",
            description = "이벤트 상태를 DELETED로 변경합니다."
    )
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId
    ) {
        adminEventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }
}