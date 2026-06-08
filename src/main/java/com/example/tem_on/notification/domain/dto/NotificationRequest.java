package com.example.tem_on.notification.domain.dto;

import com.example.tem_on.notification.domain.NotificationTemplate;
import com.example.tem_on.notification.domain.entity.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationRequest {
    
    private NotificationType type;

    private NotificationTemplate template;

    private String itemOrEventName;
}