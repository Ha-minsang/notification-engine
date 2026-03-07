package io.github.haminsang.notification.channel;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class NotificationRequest {

    private final String targetId;
    private final NotificationChannel channel;
    private final NotificationPayload payload;
    private final Priority priority;
    private final Duration ttl;

    public enum Priority {
        HIGH, NORMAL, LOW
    }
}
