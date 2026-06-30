package com.example.temon.queuestockservice.notification.domain.dto;

import com.example.temon.queuestockservice.notification.domain.entity.NotificationEntity;
import com.example.temon.queuestockservice.notification.domain.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(NotificationEntity entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}