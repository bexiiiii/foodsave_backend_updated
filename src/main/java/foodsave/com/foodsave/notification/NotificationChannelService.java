package foodsave.com.foodsave.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NotificationChannelService {
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${notification.telegram.bot-token}")
    private String telegramBotToken;

    @Value("${notification.telegram.chat-id}")
    private String telegramChatId;

    @Value("${notification.slack.webhook-url}")
    private String slackWebhookUrl;

    @Value("${notification.email.from}")
    private String emailFrom;

    @Value("${notification.email.recipients}")
    private List<String> emailRecipients;

    public NotificationChannelService(JavaMailSender mailSender, RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
    }

    public void sendEmailNotification(String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailFrom);
            helper.setTo(emailRecipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Логирование ошибки
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    public void sendTelegramNotification(String message) {
        try {
            String telegramApiUrl = String.format(
                    "https://api.telegram.org/bot%s/sendMessage",
                    telegramBotToken
            );

            Map<String, String> requestBody = Map.of(
                    "chat_id", telegramChatId,
                    "text", message,
                    "parse_mode", "HTML"
            );

            restTemplate.postForObject(telegramApiUrl, requestBody, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send Telegram notification: " + e.getMessage());
        }
    }

    public void sendSlackNotification(String message) {
        try {
            Map<String, String> requestBody = Map.of(
                    "text", message,
                    "channel", "#server-alerts"
            );

            restTemplate.postForObject(slackWebhookUrl, requestBody, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }

    public void sendSMSNotification(String phoneNumber, String message) {
        // Здесь можно добавить интеграцию с SMS-сервисом
        // Например, Twilio или другой провайдер
        System.out.println("SMS to " + phoneNumber + ": " + message);
    }
}