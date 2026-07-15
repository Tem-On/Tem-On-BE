package com.example.temon.queuestockservice.monitoring.service;

import com.example.temon.queuestockservice.monitoring.domain.dto.KafkaStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.MonitoringLogResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.MonitoringRealtimeResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.RedisStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.SystemMetricResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.WebSocketStatusResponse;
import com.example.temon.queuestockservice.monitoring.websocket.WebSocketSessionTracker;
import com.sun.management.OperatingSystemMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringService {

    private static final String LOG_FILE_PATH = "./logs/info.log";
    private static final int MAX_LOG_COUNT = 100;

    /*
     * Spring Boot 기본 로그 예시
     *
     * 2026-07-15T21:51:47.401+09:00 INFO 16956 ---
     * [temon-queue-stock-service] [restartedMain]
     * c.e.t.q.QueueStockServiceApplication : 메시지
     */
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+)\\s+" +
            "(TRACE|DEBUG|INFO|WARN|ERROR)\\s+" +
            "\\d+\\s+---\\s+" +
            "\\[[^]]*]\\s+" +
            "\\[[^]]*]\\s+" +
            "(.+?)\\s*:\\s*(.*)$"
    );

    private final AdminClient adminClient;
    private final RedisConnectionFactory redisConnectionFactory;
    private final WebSocketSessionTracker webSocketSessionTracker;

    /**
     * 프론트의 SystemMetric[] 타입에 맞는 시스템 지표를 반환합니다.
     */
    public List<SystemMetricResponse> getSystemMetrics() {
        List<SystemMetricResponse> metrics = new ArrayList<>();

        OperatingSystemMXBean osBean =
                ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        Runtime runtime = Runtime.getRuntime();
        File root = getDiskRoot();

        double cpuUsage = calculateCpuUsage(osBean);

        long jvmTotalMemory = runtime.totalMemory();
        long jvmFreeMemory = runtime.freeMemory();
        long jvmUsedMemory = jvmTotalMemory - jvmFreeMemory;

        double memoryUsage = calculatePercentage(
                jvmUsedMemory,
                jvmTotalMemory
        );

        long totalDiskSpace = root.getTotalSpace();
        long freeDiskSpace = root.getFreeSpace();
        long usedDiskSpace = totalDiskSpace - freeDiskSpace;

        double diskUsage = calculatePercentage(
                usedDiskSpace,
                totalDiskSpace
        );

        KafkaStatusResponse kafkaStatus = getKafkaStatus();
        RedisStatusResponse redisStatus = getRedisStatus();
        WebSocketStatusResponse webSocketStatus = getWebSocketStatus();

        metrics.add(new SystemMetricResponse(
                "CPU 사용률",
                formatDecimal(cpuUsage),
                "%",
                resolveUsageStatus(cpuUsage)
        ));

        metrics.add(new SystemMetricResponse(
                "JVM 메모리",
                formatDecimal(memoryUsage),
                "%",
                resolveUsageStatus(memoryUsage)
        ));

        metrics.add(new SystemMetricResponse(
                "디스크 사용률",
                formatDecimal(diskUsage),
                "%",
                resolveUsageStatus(diskUsage)
        ));

        metrics.add(new SystemMetricResponse(
                "Kafka 브로커",
                String.valueOf(kafkaStatus.getBrokerCount()),
                "개",
                "UP".equalsIgnoreCase(kafkaStatus.getStatus())
                        ? "healthy"
                        : "critical"
        ));

        metrics.add(new SystemMetricResponse(
                "Redis 연결",
                String.valueOf(redisStatus.getConnectedClients()),
                "개",
                "UP".equalsIgnoreCase(redisStatus.getStatus())
                        ? "healthy"
                        : "critical"
        ));

        metrics.add(new SystemMetricResponse(
                "WebSocket 사용자",
                String.valueOf(webSocketStatus.getConnectedUsers()),
                "명",
                "UP".equalsIgnoreCase(webSocketStatus.getStatus())
                        ? "healthy"
                        : "critical"
        ));

        return metrics;
    }

    /**
     * 로그 파일의 최근 로그를 읽어 프론트 LogEntry[] 형식으로 변환합니다.
     */
    public List<MonitoringLogResponse> getRecentLogs() {
        File logFile = new File(LOG_FILE_PATH);

        if (!logFile.exists()) {
            return List.of(createSystemLog(
                    "WARN",
                    "MonitoringService",
                    "로그 파일이 존재하지 않습니다: "
                            + logFile.getAbsolutePath()
            ));
        }

        try {
            var decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);

            List<String> allLines;

            try (
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(logFile),
                                    decoder
                            )
                    )
            ) {
                allLines = reader.lines().collect(Collectors.toList());
            }

            List<MonitoringLogResponse> result = new ArrayList<>();

            /*
             * 로그 파일의 끝부분부터 읽어서 최신 로그가 위에 오도록 합니다.
             * Kafka 설정값처럼 여러 줄로 출력되는 부가 행은 제외하고,
             * timestamp로 시작하는 실제 로그 행만 반환합니다.
             */
            for (int i = allLines.size() - 1;
                 i >= 0 && result.size() < MAX_LOG_COUNT;
                 i--) {

                MonitoringLogResponse parsedLog = parseLogLine(allLines.get(i));

                if (parsedLog != null) {
                    result.add(parsedLog);
                }
            }

            if (result.isEmpty()) {
                return List.of(createSystemLog(
                        "WARN",
                        "MonitoringService",
                        "파싱할 수 있는 로그가 없습니다."
                ));
            }

            return result;

        } catch (Exception e) {
            log.error("로그 모니터링 조회 중 오류 발생", e);

            return List.of(createSystemLog(
                    "ERROR",
                    "MonitoringService",
                    "로그를 읽는 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    public KafkaStatusResponse getKafkaStatus() {
        try {
            int brokerCount = adminClient
                    .describeCluster()
                    .nodes()
                    .get()
                    .size();

            return new KafkaStatusResponse(
                    brokerCount > 0 ? "UP" : "DOWN",
                    brokerCount
            );

        } catch (Exception e) {
            log.warn("Kafka 상태 조회 실패: {}", e.getMessage());
            return new KafkaStatusResponse("DOWN", 0);
        }
    }

    public RedisStatusResponse getRedisStatus() {
        try (
                RedisConnection connection =
                        redisConnectionFactory.getConnection()
        ) {
            String pong = connection.ping();
            Properties clientsInfo = connection.info("clients");

            long connectedClients = 0;

            if (
                    clientsInfo != null
                            && clientsInfo.getProperty("connected_clients") != null
            ) {
                connectedClients = Long.parseLong(
                        clientsInfo.getProperty("connected_clients")
                );
            }

            String status = "PONG".equalsIgnoreCase(pong)
                    ? "UP"
                    : "DOWN";

            return new RedisStatusResponse(
                    status,
                    connectedClients
            );

        } catch (Exception e) {
            log.warn("Redis 상태 조회 실패: {}", e.getMessage());
            return new RedisStatusResponse("DOWN", 0);
        }
    }

    public WebSocketStatusResponse getWebSocketStatus() {
        return new WebSocketStatusResponse(
                "UP",
                webSocketSessionTracker.getConnectedUsers()
        );
    }

    public MonitoringRealtimeResponse getRealtimeStatus() {
        return new MonitoringRealtimeResponse(
                getKafkaStatus(),
                getRedisStatus(),
                getWebSocketStatus(),
                LocalDateTime.now()
        );
    }

    private MonitoringLogResponse parseLogLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        Matcher matcher = LOG_PATTERN.matcher(line);

        if (!matcher.matches()) {
            return null;
        }

        String timestamp = matcher.group(1);
        String level = normalizeLevel(matcher.group(2));
        String source = matcher.group(3).trim();
        String message = matcher.group(4).trim();

        return new MonitoringLogResponse(
                UUID.randomUUID().toString(),
                timestamp,
                level,
                source,
                message
        );
    }

    private MonitoringLogResponse createSystemLog(
            String level,
            String source,
            String message
    ) {
        return new MonitoringLogResponse(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                normalizeLevel(level),
                source,
                message
        );
    }

    /**
     * 프론트는 INFO, WARN, ERROR만 허용하므로
     * TRACE와 DEBUG는 INFO로 변환합니다.
     */
    private String normalizeLevel(String level) {
        if (level == null) {
            return "INFO";
        }

        return switch (level.toUpperCase()) {
            case "ERROR" -> "ERROR";
            case "WARN" -> "WARN";
            default -> "INFO";
        };
    }

    private double calculateCpuUsage(OperatingSystemMXBean osBean) {
        if (osBean == null) {
            return 0.0;
        }

        double cpuLoad = osBean.getCpuLoad();

        if (Double.isNaN(cpuLoad) || cpuLoad < 0) {
            return 0.0;
        }

        return cpuLoad * 100;
    }

    private double calculatePercentage(long used, long total) {
        if (total <= 0) {
            return 0.0;
        }

        return ((double) used / total) * 100;
    }

    private String resolveUsageStatus(double usage) {
        if (usage >= 90) {
            return "critical";
        }

        if (usage >= 70) {
            return "warning";
        }

        return "healthy";
    }

    private String formatDecimal(double value) {
        return String.format(
                java.util.Locale.US,
                "%.1f",
                value
        );
    }

    private File getDiskRoot() {
        File[] roots = File.listRoots();

        if (roots != null && roots.length > 0) {
            return roots[0];
        }

        return new File("/");
    }
}