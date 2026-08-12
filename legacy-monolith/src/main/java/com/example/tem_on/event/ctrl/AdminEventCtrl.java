package com.example.tem_on.event.ctrl;

import com.example.tem_on.event.domain.dto.EventCreateRequest;
import com.example.tem_on.event.domain.dto.EventResponse;
import com.example.tem_on.event.domain.dto.EventStatusUpdateRequest;
import com.example.tem_on.event.domain.dto.EventUpdateRequest;
import com.example.tem_on.event.service.AdminEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Event", description = "관리자 - 이벤트 관리 API")
@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class AdminEventCtrl {

    private final AdminEventService adminEventService;

    @PostMapping
    @Operation(summary = "이벤트 생성", description = "새로운 선착순/타임세일 이벤트를 등록합니다.")
    public ResponseEntity<String> createEvent(@RequestBody EventCreateRequest request) {
        adminEventService.createEvent(request);
        return ResponseEntity.ok("이벤트가 성공적으로 생성되었습니다.");
    }

    @GetMapping
    @Operation(summary = "이벤트 목록 조회", description = "관리자용 전체 이벤트 목록을 조회합니다.")
    public ResponseEntity<List<EventResponse>> getEventList() {
        List<EventResponse> events = adminEventService.getEventList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "이벤트 상세 조회", description = "특정 이벤트의 상세 정보를 조회합니다.")
    public ResponseEntity<EventResponse> getEventDetail(@PathVariable("eventId") Long eventId) {
        EventResponse event = adminEventService.getEventDetail(eventId);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "이벤트 수정", description = "이벤트의 제목, 기간 등 기본 정보를 수정합니다.")
    public ResponseEntity<String> updateEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody EventUpdateRequest request) {
        adminEventService.updateEvent(eventId, request);
        return ResponseEntity.ok("이벤트 정보가 수정되었습니다.");
    }

    @PatchMapping("/{eventId}/status")
    @Operation(summary = "이벤트 상태 변경", description = "이벤트를 강제 오픈하거나 종료하는 등 상태를 변경합니다.")
    public ResponseEntity<String> updateEventStatus(
            @PathVariable("eventId") Long eventId,
            @RequestBody EventStatusUpdateRequest request) {
        adminEventService.updateEventStatus(eventId, request);
        return ResponseEntity.ok("이벤트 상태가 변경되었습니다.");
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "이벤트 삭제", description = "이벤트의 상태를 DELETED로 변경합니다.")
    public ResponseEntity<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        adminEventService.deleteEvent(eventId);
        return ResponseEntity.ok("이벤트가 DELETED상태로 변경되었습니다.");
    }
}