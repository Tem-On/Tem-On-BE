package com.example.temon.queuestockservice.notification.service;

import com.example.temon.queuestockservice.notification.domain.NotificationTemplate;
import com.example.temon.queuestockservice.notification.domain.dto.NotificationResponse;
import com.example.temon.queuestockservice.notification.domain.entity.NotificationEntity;
import com.example.temon.queuestockservice.notification.domain.entity.NotificationType;
import com.example.temon.queuestockservice.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void readNotification(Long notificationId, Long userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 알림에 대한 접근 권한이 없습니다.");
        }

        notification.markAsRead();
    }

    @Transactional
    public void readAllNotifications(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 알림을 삭제할 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }

    @Transactional
    public void sendNotification(
            Long userId,
            NotificationType type,
            NotificationTemplate template,
            Object... templateArgs
    ) {
        String title = template.getTitle();
        String message = template.createBody(templateArgs);

        NotificationEntity notification = NotificationEntity.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }
}