package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final AnalyticsService analyticsService;

    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        return ResponseEntity.ok(analyticsService.getDashboardMetrics());
    }

    @GetMapping("/sales/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    public ResponseEntity<Map<String, Object>> getMonthlySales(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().getYear()}") int year) {
        return ResponseEntity.ok(analyticsService.getMonthlySales(year));
    }

    @GetMapping("/orders/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    public ResponseEntity<List<Map<String, Object>>> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getRecentOrders(limit));
    }

    @GetMapping("/demographics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDemographics() {
        return ResponseEntity.ok(analyticsService.getDemographics());
    }
} 