package io.github.haminsang.notification.sse.pubsub;

import io.github.haminsang.notification.channel.NotificationPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    // 수신 대상 ID
    private String targetId;
    // 알림 내용
    private NotificationPayload payload;
}
