package io.github.haminsang.notification.channel;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NotificationResult {

    private final boolean success;
    private final String notificationId;
    private final NotificationChannel channel;
    private final String errorMessage;
    private final LocalDateTime sentAt;

    public static NotificationResult success(NotificationChannel channel) {
        return NotificationResult.builder()
                .success(true)
                .notificationId(UUID.randomUUID().toString())
                .channel(channel)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public static NotificationResult failure(String errorMessage, NotificationChannel channel) {
        return NotificationResult.builder()
                .success(false)
                .channel(channel)
                .errorMessage(errorMessage)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
