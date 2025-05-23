package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getNotifications() {
        return ResponseEntity.ok(notificationService.getRecentAlerts());
    }

    @PostMapping("/alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendAlert(@RequestBody Map<String, String> alert) {
        notificationService.sendSystemAlert(
            alert.get("message"),
            alert.get("severity")
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role-change")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> notifyRoleChange(
            @RequestParam String roleName,
            @RequestParam String action,
            @RequestParam String adminEmail) {
        notificationService.sendRoleChangeNotification(roleName, action, adminEmail);
        return ResponseEntity.ok().build();
    }
} 