package com.example.tem_on.notification.domain.entity;

public enum NotificationType {
    ORDER_SUCCESS,  // 주문/결제 성공 알림
    STOCK_ALERT,    // 재고 부족/품절 경고 알림
    EVENT_OPEN,     // 기획전 오픈 알림
    MARKETING       // 일반 공지 및 마케팅 알림
}