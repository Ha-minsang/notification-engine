package io.github.haminsang.notification.core;

import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import io.github.haminsang.notification.exception.NotificationException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

public class NotificationDispatcher implements NotificationEngine {

    private final Map<io.github.haminsang.notification.channel.NotificationChannel, NotificationSender> senderMap;

    public NotificationDispatcher(List<NotificationSender> senders) {
        this.senderMap = senders.stream()
                .collect(Collectors.toMap(NotificationSender::supportedChannel, Function.identity()));
    }

    @Override
    public CompletableFuture<NotificationResult> send(NotificationRequest request) {
        return findSender(request).send(request);
    }

    @Override
    public CompletableFuture<List<NotificationResult>> sendBulk(List<NotificationRequest> requests) {
        List<CompletableFuture<NotificationResult>> futures = requests.stream()
                .map(request -> findSender(request).send(request))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    // 채널에 맞는 Sender 탐색
    private NotificationSender findSender(NotificationRequest request) {
        NotificationSender sender = senderMap.get(request.getChannel());
        if (sender == null) {
            throw new NotificationException("지원하지 않는 채널: " + request.getChannel());
        }
        return sender;
    }
}