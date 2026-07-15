package com.example.temon.queuestockservice.monitoring.ctrl;

import com.example.temon.queuestockservice.monitoring.domain.dto.KafkaStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.MonitoringLogResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.RedisStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.SystemMetricResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.WebSocketStatusResponse;
import com.example.temon.queuestockservice.monitoring.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Monitoring",
        description = "관리자용 시스템 인프라 및 핵심 컴포넌트 모니터링 API"
)
public class MonitoringCtrl {

    private final MonitoringService monitoringService;

    @GetMapping("/metrics")
    @Operation(summary = "CPU, 메모리, 디스크 및 인프라 지표 조회")
    public ResponseEntity<List<SystemMetricResponse>> getSystemMetrics() {
        return ResponseEntity.ok(
                monitoringService.getSystemMetrics()
        );
    }

    @GetMapping("/logs")
    @Operation(summary = "시스템 최근 이벤트 및 에러 로그 조회")
    public ResponseEntity<List<MonitoringLogResponse>> getRecentLogs() {
        return ResponseEntity.ok(
                monitoringService.getRecentLogs()
        );
    }

    @GetMapping("/kafka")
    @Operation(summary = "Kafka 상태 조회")
    public ResponseEntity<KafkaStatusResponse> getKafkaStatus() {
        return ResponseEntity.ok(
                monitoringService.getKafkaStatus()
        );
    }

    @GetMapping("/redis")
    @Operation(summary = "Redis 상태 조회")
    public ResponseEntity<RedisStatusResponse> getRedisStatus() {
        return ResponseEntity.ok(
                monitoringService.getRedisStatus()
        );
    }

    @GetMapping("/websocket")
    @Operation(summary = "WebSocket 상태 조회")
    public ResponseEntity<WebSocketStatusResponse> getWebSocketStatus() {
        return ResponseEntity.ok(
                monitoringService.getWebSocketStatus()
        );
    }
}