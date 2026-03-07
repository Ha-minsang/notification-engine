package io.github.haminsang.notification.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {
    private String title;
    private String body;
    private Map<String, String> metadata;
}
