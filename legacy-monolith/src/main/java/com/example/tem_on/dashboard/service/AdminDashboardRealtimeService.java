package com.example.tem_on.dashboard.service;

import com.example.tem_on.dashboard.domain.dto.AdminDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardRealtimeService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AdminDashboardService adminDashboardService;

    public void publishDashboard() {
        AdminDashboardResponse response = adminDashboardService.getDashboard();
        messagingTemplate.convertAndSend("/topic/admin/dashboard", response);
    }
}