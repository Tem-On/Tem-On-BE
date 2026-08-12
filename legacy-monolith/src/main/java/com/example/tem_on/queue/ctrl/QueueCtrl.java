package com.example.tem_on.queue.ctrl;

import com.example.tem_on.global.util.SecurityUtil;
import com.example.tem_on.queue.domain.dto.QueueAvailableResponse;
import com.example.tem_on.queue.domain.dto.QueueCurrentUsersResponse;
import com.example.tem_on.queue.domain.dto.QueueEnterResponse;
import com.example.tem_on.queue.domain.dto.QueueEstimatedTimeResponse;
import com.example.tem_on.queue.domain.dto.QueueRankResponse;
import com.example.tem_on.queue.domain.dto.QueueStatusResponse;
import com.example.tem_on.queue.domain.dto.QueueTestEnterRequest;
import com.example.tem_on.queue.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/queue")
public class QueueCtrl {

    private final QueueService queueService;

    @Operation(summary = "대기열 진입", description = "현재 로그인한 사용자를 이벤트 상품 대기열에 진입시킵니다.")
    @PostMapping("/enter")
    public ResponseEntity<QueueEnterResponse> enter(
            @RequestParam Long eventProductId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                queueService.enter(eventProductId, userId)
        );
    }

    @Operation(summary = "현재 대기 순번 조회", description = "현재 로그인한 사용자의 대기 순번을 조회합니다.")
    @GetMapping("/rank")
    public ResponseEntity<QueueRankResponse> getRank(
            @RequestParam Long eventProductId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                queueService.getRank(eventProductId, userId)
        );
    }

    @Operation(summary = "대기 상태 조회", description = "현재 로그인한 사용자의 대기 상태를 조회합니다.")
    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getStatus(
            @RequestParam Long eventProductId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                queueService.getStatus(eventProductId, userId)
        );
    }

    @Operation(summary = "구매 가능 여부 조회", description = "현재 로그인한 사용자가 구매 가능한 상태인지 조회합니다.")
    @GetMapping("/available")
    public ResponseEntity<QueueAvailableResponse> getAvailable(
            @RequestParam Long eventProductId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                queueService.getAvailable(eventProductId, userId)
        );
    }

    @Operation(summary = "예상 대기 시간 조회", description = "현재 로그인한 사용자의 예상 대기 시간을 초 단위로 조회합니다.")
    @GetMapping("/estimated-time")
    public ResponseEntity<QueueEstimatedTimeResponse> getEstimatedTime(
            @RequestParam Long eventProductId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                queueService.getEstimatedTime(eventProductId, userId)
        );
    }

    @Operation(summary = "현재 대기 인원 조회", description = "이벤트 상품의 현재 대기 인원을 조회합니다.")
    @GetMapping("/current-users")
    public ResponseEntity<QueueCurrentUsersResponse> getCurrentUsers(
            @RequestParam Long eventProductId
    ) {
        return ResponseEntity.ok(
                queueService.getCurrentUsers(eventProductId)
        );
    }

    @Operation(summary = "대기열 만료 처리", description = "대기열 앞 순번 사용자를 구매 가능 상태로 전환합니다.")
    @PostMapping("/expire")
    public ResponseEntity<Void> expire(
            @RequestParam Long eventProductId
    ) {
        queueService.expire(eventProductId);
        return ResponseEntity.ok().build();
    }

    // 대기열 테스트용 API
    @Operation(
            summary = "테스트용 대기열 진입",
            description = "부하 테스트용 API입니다."
    )
    @PostMapping("/test-enter")
    public ResponseEntity<QueueEnterResponse> testEnter(
            @RequestBody QueueTestEnterRequest request
    ) {

        return ResponseEntity.ok(
                queueService.enter(
                        request.getEventProductId(),
                        request.getUserId()
                )
        );
    }
}