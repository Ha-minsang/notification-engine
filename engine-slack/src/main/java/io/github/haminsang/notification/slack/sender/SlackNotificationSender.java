package io.github.haminsang.notification.slack.sender;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import io.github.haminsang.notification.core.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SlackNotificationSender implements NotificationSender {

    private final WebClient webClient;

    public SlackNotificationSender(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public CompletableFuture<NotificationResult> send(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String text = "*" + request.getPayload().getTitle() + "*\n" + request.getPayload().getBody();
                webClient.post()
                        .uri(request.getTargetId()) // targetId가 Webhook URL
                        .bodyValue(Map.of("text", text))
                        .retrieve()
                        .toBodilessEntity()
                        .block();
                return NotificationResult.success(NotificationChannel.SLACK);
            } catch (Exception e) {
                log.error("Slack 발송 실패: targetId={}", request.getTargetId(), e);
                return NotificationResult.failure(e.getMessage(), NotificationChannel.SLACK);
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
        return NotificationChannel.SLACK;
    }
}