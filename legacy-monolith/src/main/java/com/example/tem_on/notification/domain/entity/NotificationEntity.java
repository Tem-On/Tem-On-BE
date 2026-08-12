package com.example.tem_on.notification.domain.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) 
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "user_id", nullable = false)
    private Long userId; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type; 

    @Column(nullable = false, length = 100)
    private String title; 

    @Column(nullable = false, length = 500)
    private String message; 

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;


    public void markAsRead() {
        this.isRead = true;
    }
}