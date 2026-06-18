package com.example.tem_on.monitoring.scheduler;

import com.example.tem_on.monitoring.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringScheduler {

    private final MonitoringService monitoringService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 3000)
    public void publishMonitoringStatus() {
        messagingTemplate.convertAndSend(
                "/topic/monitoring",
                monitoringService.getRealtimeStatus()
        );
    }
}