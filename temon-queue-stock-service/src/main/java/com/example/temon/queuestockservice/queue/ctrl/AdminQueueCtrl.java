package com.example.temon.queuestockservice.queue.ctrl;

import com.example.temon.queuestockservice.queue.domain.dto.AdminQueueResponse;
import com.example.temon.queuestockservice.queue.service.AdminQueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Queue", description = "관리자 대기열 관리 api")
@RestController
@RequestMapping("/api/admin/queue")
@RequiredArgsConstructor
public class AdminQueueCtrl {

    private final AdminQueueService adminQueueService;

    @Operation(summary = "이벤트 상품 대기열 조회")
    @GetMapping("/{eventProductId}")
    public ResponseEntity<AdminQueueResponse> getQueue(
            @PathVariable Long eventProductId
    ) {
        return ResponseEntity.ok(
                adminQueueService.getQueue(eventProductId)
        );
    }

    @Operation(summary = "대기열 진입 허용")
    @PatchMapping("/{eventProductId}/open")
    public ResponseEntity<AdminQueueResponse> openQueue(
            @PathVariable Long eventProductId
    ) {
        return ResponseEntity.ok(
                adminQueueService.openQueue(eventProductId)
        );
    }

    @Operation(summary = "대기열 진입 차단")
    @PatchMapping("/{eventProductId}/close")
    public ResponseEntity<AdminQueueResponse> closeQueue(
            @PathVariable Long eventProductId
    ) {
        return ResponseEntity.ok(
                adminQueueService.closeQueue(eventProductId)
        );
    }

    @Operation(summary = "대기열 초기화")
    @DeleteMapping("/{eventProductId}")
    public ResponseEntity<Void> clearQueue(
            @PathVariable Long eventProductId
    ) {
        adminQueueService.clearQueue(eventProductId);
        return ResponseEntity.noContent().build();
    }
}