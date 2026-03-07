package io.github.haminsang.notification.core;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationSender {

    // 단건 발송
    CompletableFuture<NotificationResult> send(NotificationRequest request);

    // 다건 발송
    CompletableFuture<List<NotificationResult>> sendBulk(List<NotificationRequest> requests);

    // 담당 채널 반환
    NotificationChannel supportedChannel();
}