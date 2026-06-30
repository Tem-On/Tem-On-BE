package com.example.temon.queuestockservice.monitoring.service;

import com.example.temon.queuestockservice.monitoring.domain.dto.KafkaStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.MonitoringRealtimeResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.RedisStatusResponse;
import com.example.temon.queuestockservice.monitoring.domain.dto.WebSocketStatusResponse;
import com.example.temon.queuestockservice.monitoring.websocket.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final AdminClient adminClient;
    private final RedisConnectionFactory redisConnectionFactory;
    private final WebSocketSessionTracker webSocketSessionTracker;

    public Map<String, Object> getSystemStatus() {
        Map<String, Object> statusMap = new LinkedHashMap<>();

        OperatingSystemMXBean osBean =
                ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        Runtime runtime = Runtime.getRuntime();
        File root = new File("/");

        double cpuLoad = osBean.getCpuLoad() * 100;
        String cpuUsage = String.format("%.1f%%", Double.isNaN(cpuLoad) ? 0.0 : cpuLoad);

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        String memoryUsage = String.format("%.1f%%", ((double) usedMemory / totalMemory) * 100);

        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;
        String diskUsage = String.format("%.1f%%", ((double) usedSpace / totalSpace) * 100);

        statusMap.put("status", "HEALTHY");
        statusMap.put("cpuUsage", cpuUsage);
        statusMap.put("memoryUsage", memoryUsage);
        statusMap.put("diskUsage", diskUsage);
        statusMap.put("jvmTotalMemory", (totalMemory / 1024 / 1024) + " MB");
        statusMap.put("jvmUsedMemory", (usedMemory / 1024 / 1024) + " MB");

        return statusMap;
    }

    public List<Map<String, Object>> getRecentLogs() {
        List<Map<String, Object>> logList = new ArrayList<>();
        String logFilePath = "./logs/info.log";

        try {
            File logFile = new File(logFilePath);

            if (!logFile.exists()) {
                return List.of(Map.of(
                        "timestamp", "SYSTEM",
                        "level", "WARN",
                        "message", "로그 파일이 지정된 경로에 존재하지 않습니다: " + logFilePath
                ));
            }

            var decoder = java.nio.charset.StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(java.nio.charset.CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPLACE);

            try (java.io.BufferedReader reader =
                         new java.io.BufferedReader(
                                 new java.io.InputStreamReader(
                                         new java.io.FileInputStream(logFile),
                                         decoder
                                 )
                         )) {

                List<String> allLines = reader.lines().collect(Collectors.toList());
                int totalLines = allLines.size();
                int startRow = Math.max(0, totalLines - 50);

                for (int i = totalLines - 1; i >= startRow; i--) {
                    Map<String, Object> logEntry = new LinkedHashMap<>();
                    logEntry.put("raw", allLines.get(i));
                    logList.add(logEntry);
                }
            }

        } catch (Exception e) {
            log.error("로그 모니터링 조회 중 오류 발생", e);
            logList.add(Map.of(
                    "error",
                    "로그를 읽어오는 중 에러가 발생했습니다: " + e.getMessage()
            ));
        }

        return logList;
    }

    public KafkaStatusResponse getKafkaStatus() {
        try {
            int brokerCount = adminClient.describeCluster()
                    .nodes()
                    .get()
                    .size();

            return new KafkaStatusResponse("UP", brokerCount);

        } catch (Exception e) {
            return new KafkaStatusResponse("DOWN", 0);
        }
    }

    public RedisStatusResponse getRedisStatus() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {

            String pong = connection.ping();

            Properties clientsInfo = connection.info("clients");

            long connectedClients = 0;

            if (clientsInfo != null && clientsInfo.getProperty("connected_clients") != null) {
                connectedClients = Long.parseLong(
                        clientsInfo.getProperty("connected_clients")
                );
            }

            String status = "PONG".equalsIgnoreCase(pong) ? "UP" : "DOWN";

            return new RedisStatusResponse(status, connectedClients);

        } catch (Exception e) {
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
}