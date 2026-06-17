package com.example.tem_on.monitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MonitoringService {

    public Map<String, Object> getSystemStatus() {
        Map<String, Object> statusMap = new LinkedHashMap<>();
        
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();
        File root = new File("/");

        double cpuCpuLoad = osBean.getCpuLoad() * 100;
        String cpuUsage = String.format("%.1f%%", Double.isNaN(cpuCpuLoad) ? 0.0 : cpuCpuLoad);

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        String memoryUsage = String.format("%.1f%%", ((double) usedMemory / totalMemory) * 100);
   
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usableSpace = totalSpace - freeSpace;
        String diskUsage = String.format("%.1f%%", ((double) usableSpace / totalSpace) * 100);

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

            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(new java.io.FileInputStream(logFile), decoder))) {
                
                List<String> allLines = reader.lines().collect(Collectors.toList());
                int totalLines = allLines.size();
                int startRow = Math.max(0, totalLines - 50); 

                for (int i = totalLines - 1; i >= startRow; i--) {
                    Map<String, Object> logEntry = new LinkedHashMap<>();
                    String rawLine = allLines.get(i);
                    
                    logEntry.put("raw", rawLine);
                    logList.add(logEntry);
                }
            }
        } catch (Exception e) {
            log.error("로그 모니터링 조회 중 오류 발생", e);
            logList.add(Map.of("error", "로그를 읽어오는 중 에러가 발생했습니다: " + e.getMessage()));
        }

        return logList;
    }
}