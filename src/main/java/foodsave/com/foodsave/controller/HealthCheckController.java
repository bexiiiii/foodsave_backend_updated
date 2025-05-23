package foodsave.com.foodsave.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        // Проверка памяти
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        healthInfo.put("heapMemoryUsage", memoryBean.getHeapMemoryUsage());
        healthInfo.put("nonHeapMemoryUsage", memoryBean.getNonHeapMemoryUsage());
        
        // Проверка потоков
        healthInfo.put("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        
        // Проверка времени работы
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        healthInfo.put("uptime", uptime);
        
        // Проверка загрузки системы
        double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        healthInfo.put("systemLoad", systemLoad);
        
        // Проверка доступной памяти
        Runtime runtime = Runtime.getRuntime();
        healthInfo.put("freeMemory", runtime.freeMemory());
        healthInfo.put("totalMemory", runtime.totalMemory());
        healthInfo.put("maxMemory", runtime.maxMemory());
        
        return ResponseEntity.ok(healthInfo);
    }
} 