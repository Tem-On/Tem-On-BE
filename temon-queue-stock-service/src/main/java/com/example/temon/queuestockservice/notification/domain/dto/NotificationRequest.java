package com.example.temon.queuestockservice.notification.domain.dto;

import com.example.temon.queuestockservice.notification.domain.NotificationTemplate;
import com.example.temon.queuestockservice.notification.domain.entity.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationRequest {
    
    private NotificationType type;

    private NotificationTemplate template;

    private String itemOrEventName;
}