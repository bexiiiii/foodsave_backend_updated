package foodsave.com.foodsave.monitoring;

import foodsave.com.foodsave.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ServerMonitoringService {
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;
    private final AtomicBoolean isServerDown = new AtomicBoolean(false);
    private static final String[] ENDPOINTS_TO_CHECK = {
            "/api/health",
            "/api/roles",
            "/api/stores"
    };

    @Scheduled(fixedRate = 30000) // Проверка каждые 30 секунд
    public void checkServerHealth() {
        boolean anyEndpointDown = false;
        StringBuilder errorMessage = new StringBuilder("Server health check failed:\n");

        for (String endpoint : ENDPOINTS_TO_CHECK) {
            try {
                restTemplate.getForObject(endpoint, String.class);
            } catch (Exception e) {
                anyEndpointDown = true;
                errorMessage.append("- ").append(endpoint).append(" is not responding\n");
            }
        }

        if (anyEndpointDown && !isServerDown.get()) {
            isServerDown.set(true);
            notificationService.sendSystemAlert(
                    errorMessage.toString(),
                    "error"
            );
            // Отправка уведомления администраторам
            notificationService.sendRoleChangeNotification(
                    "SYSTEM",
                    "SERVER_DOWN",
                    "admin@foodsave.com"
            );
        } else if (!anyEndpointDown && isServerDown.get()) {
            isServerDown.set(false);
            notificationService.sendSystemAlert(
                    "Server is back online",
                    "success"
            );
        }
    }

    @Scheduled(fixedRate = 300000) // Проверка каждые 5 минут
    public void checkSystemResources() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercentage = (usedMemory * 100.0) / totalMemory;

        if (memoryUsagePercentage > 80) {
            notificationService.sendSystemAlert(
                    String.format("High memory usage: %.2f%%", memoryUsagePercentage),
                    "warning"
            );
        }

        // Проверка дискового пространства
        java.io.File root = new java.io.File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        double diskUsagePercentage = ((totalSpace - freeSpace) * 100.0) / totalSpace;

        if (diskUsagePercentage > 85) {
            notificationService.sendSystemAlert(
                    String.format("High disk usage: %.2f%%", diskUsagePercentage),
                    "warning"
            );
        }
    }

    public void logError(String errorMessage, String stackTrace) {
        notificationService.sendSystemAlert(
                String.format("Error occurred: %s\nStack trace: %s", errorMessage, stackTrace),
                "error"
        );
    }
}