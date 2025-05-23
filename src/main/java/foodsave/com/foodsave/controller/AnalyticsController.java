package foodsave.com.foodsave.controller;

import  foodsave.com.foodsave.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:3001/")

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGeneralAnalytics() {
        return ResponseEntity.ok(analyticsService.getGeneralAnalytics());
    }

    @GetMapping("/sales/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    public ResponseEntity<Map<String, Object>> getSalesAnalytics(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "week") String period) {
        return ResponseEntity.ok(analyticsService.getSalesAnalytics(storeId, period));
    }

    @GetMapping("/products/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    public ResponseEntity<Map<String, Object>> getProductAnalytics(@PathVariable Long storeId) {
        return ResponseEntity.ok(analyticsService.getProductAnalytics(storeId));
    }

    @GetMapping("/discounts/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    public ResponseEntity<Map<String, Object>> getDiscountAnalytics(@PathVariable Long storeId) {
        return ResponseEntity.ok(analyticsService.getDiscountAnalytics(storeId));
    }

    @GetMapping("/users/{storeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserAnalytics(@PathVariable Long storeId) {
        return ResponseEntity.ok(analyticsService.getUserAnalytics(storeId));
    }
} 