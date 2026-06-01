package com.example.tem_on.notification.ctrl;

import com.example.tem_on.auth.jwt.CustomUserDetails; 
import com.example.tem_on.notification.domain.dto.NotificationResponse;
import com.example.tem_on.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "실시간 푸시 및 알림 내역 관리 API")
public class NotificationCtrl {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "내 알림 목록 조회", description = "로그인한 유저의 전체 알림 내역을 최신순으로 조회합니다.")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) { 
        
        return ResponseEntity.ok(notificationService.getNotifications(userDetails.getUserId()));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "안 읽은 알림 수 조회", description = "아직 읽지 않은 알림의 총 개수를 반환합니다.")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        return ResponseEntity.ok(notificationService.getUnreadCount(userDetails.getUserId()));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "단건 알림 읽음 처리", description = "특정 알림을 읽음 상태(isRead = true)로 변경합니다.")
    public ResponseEntity<String> readNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        notificationService.readNotification(notificationId, userDetails.getUserId());
        return ResponseEntity.ok("알림 읽음 처리 완료");
    }

    @PatchMapping("/read-all")
    @Operation(summary = "전체 알림 읽음 처리", description = "내가 받은 모든 알림을 한 번에 읽음 상태로 변경합니다.")
    public ResponseEntity<String> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        notificationService.readAllNotifications(userDetails.getUserId());
        return ResponseEntity.ok("모든 알림 읽음 처리 완료");
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "특정 알림 삭제", description = "내 알림 내역에서 특정 알림을 영구 삭제합니다.")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        notificationService.deleteNotification(notificationId, userDetails.getUserId());
        return ResponseEntity.ok("알림 삭제 완료");
    }
}