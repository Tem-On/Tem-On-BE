package com.example.tem_on.monitoring.ctrl;

import com.example.tem_on.monitoring.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring", description = "관리자용 시스템 인프라 및 핵심 컴포넌트 모니터링 API")
public class MonitoringCtrl {

    private final MonitoringService monitoringService; 

    @GetMapping("/system")
    @Operation(summary = "CPU/메모리 등 서버 자원 상태 조회", description = "현재 애플리케이션 서버의 실제 CPU 사용량, JVM 힙 메모리 잔여량, 디스크 용량 정보를 실시간 반환합니다.")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        return ResponseEntity.ok(monitoringService.getSystemStatus());
    }

    @GetMapping("/logs")
    @Operation(summary = "시스템 최근 이벤트 및 에러 로그 조회", description = "서버 디스크에 파일로 적재된 로그 파일의 최신 트랜잭션 50줄을 역순(최신순)으로 읽어옵니다.")
    public ResponseEntity<List<Map<String, Object>>> getRecentLogs() {
        return ResponseEntity.ok(monitoringService.getRecentLogs());
    }
}