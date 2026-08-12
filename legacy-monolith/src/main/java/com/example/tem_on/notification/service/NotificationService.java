package com.example.tem_on.notification.service;

import com.example.tem_on.notification.domain.NotificationTemplate;
import com.example.tem_on.notification.domain.dto.NotificationResponse;
import com.example.tem_on.notification.domain.entity.NotificationEntity;
import com.example.tem_on.notification.domain.entity.NotificationType;
import com.example.tem_on.notification.repository.NotificationRepository;
import com.example.tem_on.user.domain.entity.UserEntity;
import com.example.tem_on.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
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
            throw new StringIndexOutOfBoundsException("해당 알림에 대한 접근 권한이 없습니다.");
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
    public void sendNotification(Long userId, NotificationType type, NotificationTemplate template, Object... templateArgs) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

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

        if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
            fcmService.sendPushNotification(user.getFcmToken(), title, message);
        }
    }
}