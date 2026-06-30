package com.example.temon.queuestockservice.monitoring.ctrl;

import com.example.temon.queuestockservice.monitoring.domain.dto.KafkaStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.RedisStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.WebSocketStatusResponse;
import com.example.temon.queuestockservice.monitoring.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Monitoring", description = "관리자용 시스템 인프라 및 핵심 컴포넌트 모니터링 API")
public class MonitoringCtrl {

    private final MonitoringService monitoringService;

    @GetMapping("/system")
    @Operation(summary = "CPU/메모리 등 서버 자원 상태 조회")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        return ResponseEntity.ok(monitoringService.getSystemStatus());
    }

    @GetMapping("/logs")
    @Operation(summary = "시스템 최근 이벤트 및 에러 로그 조회")
    public ResponseEntity<List<Map<String, Object>>> getRecentLogs() {
        return ResponseEntity.ok(monitoringService.getRecentLogs());
    }

    @GetMapping("/kafka")
    @Operation(summary = "Kafka 상태 조회")
    public ResponseEntity<KafkaStatusResponse> getKafkaStatus() {
        return ResponseEntity.ok(monitoringService.getKafkaStatus());
    }

    @GetMapping("/redis")
    @Operation(summary = "Redis 상태 조회")
    public ResponseEntity<RedisStatusResponse> getRedisStatus() {
        return ResponseEntity.ok(monitoringService.getRedisStatus());
    }

    @GetMapping("/websocket")
    @Operation(summary = "WebSocket 상태 조회")
    public ResponseEntity<WebSocketStatusResponse> getWebSocketStatus() {
        return ResponseEntity.ok(monitoringService.getWebSocketStatus());
    }
}