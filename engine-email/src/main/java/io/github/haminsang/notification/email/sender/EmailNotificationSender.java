package io.github.haminsang.notification.email.sender;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import io.github.haminsang.notification.core.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailNotificationSender(JavaMailSender mailSender, String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public CompletableFuture<NotificationResult> send(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(request.getTargetId());
                message.setSubject(request.getPayload().getTitle());
                message.setText(request.getPayload().getBody());
                mailSender.send(message);
                return NotificationResult.success(NotificationChannel.EMAIL);
            } catch (Exception e) {
                log.error("Email 발송 실패: targetId={}", request.getTargetId(), e);
                return NotificationResult.failure(e.getMessage(), NotificationChannel.EMAIL);
            }
        });
    }

    @Override
    public CompletableFuture<List<NotificationResult>> sendBulk(List<NotificationRequest> requests) {
        List<CompletableFuture<NotificationResult>> futures = requests.stream()
                .map(this::send)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.EMAIL;
    }
}