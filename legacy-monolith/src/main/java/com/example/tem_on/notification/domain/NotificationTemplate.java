package com.example.tem_on.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationTemplate {
    
    ORDER_SUCCESS(
        "주문 완료 안내", 
        "고객님이 주문하신 [%s] 상품의 결제가 정상 완료되었습니다."
    ),
    STOCK_ALERT(
        "재고 부족 경고", 
        "현재 [ID: %d] 상품의 재고가 %d개 남았습니다. 빠른 조치가 필요합니다!"
    ),
    EVENT_OPEN(
        "기획전 오픈 알림", 
        "기다리시던 [%s] 기획전이 지금 막 오픈되었습니다! 한정 수량을 놓치지 마세요."
    ),
    MARKETING(
        "TEM-ON 특별 혜택", 
        "지금 선착순 타임딜 진행 중! 최대 %d%% 할인 쿠폰이 발급되었습니다."
    );

    private final String title;
    private final String bodyTemplate;

    NotificationTemplate(String title, String bodyTemplate) {
        this.title = title;
        this.bodyTemplate = bodyTemplate;
    }
    
    public String createBody(Object... args) {
        return String.format(this.bodyTemplate, args);
    }
}