package com.example.tem_on.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    /**
     * 특정 디바이스 토큰을 가진 기기로 실시간 푸시 알림 발송
     *
     * @param targetToken 프론트엔드가 발급받아 백엔드에 넘겨준 고유 FCM 토큰
     * @param title       알림 팝업 제목
     * @param body        알림 팝업 내용
     */
    public void sendPushNotification(String targetToken, String title, String body) {
        try {
            // 1. 알림 내용 조립
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // 2. FCM 메시지 생성
            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(notification)
                    .build();

            // 3. 구글 FCM 서버로 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("🚀 FCM 푸시 발송 요청 성공! Message ID: {}", response);

        } catch (Exception e) {
            log.error("❌ FCM 푸시 발송 실패: targetToken={}, error={}", targetToken, e.getMessage(), e);
        }
    }
}