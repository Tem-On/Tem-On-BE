package com.example.temon.queuestockservice.queue.ctrl;

import com.example.temon.queuestockservice.global.jwt.ApiUserPrincipal;
import com.example.temon.queuestockservice.queue.domain.dto.QueueAvailableResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueCurrentUsersResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueEnterResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueEstimatedTimeResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueRankResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueStatusResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueTestEnterRequest;
import com.example.temon.queuestockservice.queue.domain.dto.QueueTestMultipleEnterRequest;
import com.example.temon.queuestockservice.queue.domain.dto.QueueTestMultipleEnterResponse;
import com.example.temon.queuestockservice.queue.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/queue")
public class QueueCtrl {

    private final QueueService queueService;

    @PostMapping("/enter")
    @Operation(
            summary = "대기열 진입",
            description = "현재 로그인한 사용자를 이벤트 상품 대기열에 진입시킵니다."
    )
    public ResponseEntity<QueueEnterResponse> enter(
            @RequestParam Long eventProductId,
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                queueService.enter(
                        eventProductId,
                        userPrincipal.getUserId()
                )
        );
    }

    @GetMapping("/rank")
    @Operation(
            summary = "현재 대기 순번 조회",
            description = "현재 로그인한 사용자의 대기 순번을 조회합니다."
    )
    public ResponseEntity<QueueRankResponse> getRank(
            @RequestParam Long eventProductId,
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                queueService.getRank(
                        eventProductId,
                        userPrincipal.getUserId()
                )
        );
    }

    @GetMapping("/status")
    @Operation(
            summary = "대기 상태 조회",
            description = "현재 로그인한 사용자의 대기 상태를 조회합니다."
    )
    public ResponseEntity<QueueStatusResponse> getStatus(
            @RequestParam Long eventProductId,
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                queueService.getStatus(
                        eventProductId,
                        userPrincipal.getUserId()
                )
        );
    }

    @GetMapping("/available")
    @Operation(
            summary = "구매 가능 여부 조회",
            description = "현재 로그인한 사용자가 구매 가능한 상태인지 조회합니다."
    )
    public ResponseEntity<QueueAvailableResponse> getAvailable(
            @RequestParam Long eventProductId,
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                queueService.getAvailable(
                        eventProductId,
                        userPrincipal.getUserId()
                )
        );
    }

    @GetMapping("/estimated-time")
    @Operation(
            summary = "예상 대기 시간 조회",
            description = "현재 로그인한 사용자의 예상 대기 시간을 초 단위로 조회합니다."
    )
    public ResponseEntity<QueueEstimatedTimeResponse> getEstimatedTime(
            @RequestParam Long eventProductId,
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                queueService.getEstimatedTime(
                        eventProductId,
                        userPrincipal.getUserId()
                )
        );
    }

    @GetMapping("/current-users")
    @Operation(
            summary = "현재 대기 인원 조회",
            description = "이벤트 상품의 현재 대기 인원을 조회합니다."
    )
    public ResponseEntity<QueueCurrentUsersResponse> getCurrentUsers(
            @RequestParam Long eventProductId
    ) {
        return ResponseEntity.ok(
                queueService.getCurrentUsers(eventProductId)
        );
    }

    @PostMapping("/expire")
    @Operation(
            summary = "대기열 만료 처리",
            description = "대기열 앞 순번 사용자를 구매 가능 상태로 전환합니다."
    )
    public ResponseEntity<Void> expire(
            @RequestParam Long eventProductId
    ) {
        queueService.expire(eventProductId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test-enter")
    @Operation(
            summary = "테스트용 대기열 진입",
            description = "부하 테스트용으로 사용자 한 명을 대기열에 진입시킵니다."
    )
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

    @PostMapping("/test-enter-multiple")
    @Operation(
            summary = "테스트 사용자 여러 명 대기열 진입",
            description = "지정한 시작 사용자 ID부터 여러 명을 Redis 대기열에 직접 추가합니다."
    )
    public ResponseEntity<QueueTestMultipleEnterResponse>
    testEnterMultiple(
            @RequestBody QueueTestMultipleEnterRequest request
    ) {
        if (request.getCount() == null) {
            throw new IllegalArgumentException(
                    "count는 필수입니다."
            );
        }

        long addedCount =
                queueService.testEnterMultiple(
                        request.getEventProductId(),
                        request.getStartUserId(),
                        request.getCount()
                );

        return ResponseEntity.ok(
                new QueueTestMultipleEnterResponse(
                        request.getEventProductId(),
                        request.getStartUserId(),
                        request.getCount(),
                        addedCount
                )
        );
    }
}