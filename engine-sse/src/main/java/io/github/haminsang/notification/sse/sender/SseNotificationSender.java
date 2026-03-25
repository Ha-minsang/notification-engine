package io.github.haminsang.notification.sse.sender;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import io.github.haminsang.notification.core.NotificationSender;
import io.github.haminsang.notification.sse.pubsub.NotificationMessage;
import io.github.haminsang.notification.sse.pubsub.NotificationPublisher;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SseNotificationSender implements NotificationSender {

    private final NotificationPublisher publisher;

    public SseNotificationSender(NotificationPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public CompletableFuture<NotificationResult> send(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                publisher.publish(NotificationMessage.builder()
                        .targetId(request.getTargetId())
                        .payload(request.getPayload())
                        .build());
                return NotificationResult.success(NotificationChannel.SSE);
            } catch (Exception e) {
                log.error("SSE 알림 발송 실패: targetId={}", request.getTargetId(), e);
                return NotificationResult.failure(e.getMessage(), NotificationChannel.SSE);
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
        return NotificationChannel.SSE;
    }
}