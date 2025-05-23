package foodsave.com.foodsave.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.data.redis.core.RedisTemplate;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Async
    public void sendRoleChangeNotification(String roleName, String action, String adminEmail) {
        String subject = "Role Management Alert";
        String content = String.format("Role '%s' was %s by administrator", roleName, action);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(adminEmail);
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error and store in Redis for retry
            redisTemplate.opsForList().rightPush("failed_notifications",
                    String.format("%s|%s|%s", roleName, action, adminEmail));
        }
    }

    public void sendSystemAlert(String message, String severity) {
        // Store alert in Redis for real-time dashboard updates
        redisTemplate.opsForList().rightPush("system_alerts",
                String.format("%s|%s|%d", message, severity, System.currentTimeMillis()));
        redisTemplate.expire("system_alerts", 24, TimeUnit.HOURS);
    }

    public List<String> getRecentAlerts() {
        return redisTemplate.opsForList().range("system_alerts", 0, -1);
    }

    @Async
    public void processFailedNotifications() {
        String failedNotification;
        while ((failedNotification = redisTemplate.opsForList().leftPop("failed_notifications")) != null) {
            String[] parts = failedNotification.split("\\|");
            if (parts.length == 3) {
                sendRoleChangeNotification(parts[0], parts[1], parts[2]);
            }
        }
    }
}