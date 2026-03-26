package io.github.haminsang.notification.config;

import io.github.haminsang.notification.core.NotificationDispatcher;
import io.github.haminsang.notification.core.NotificationEngine;
import io.github.haminsang.notification.core.NotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NotificationAutoConfiguration {

    // 등록된 모든 Sender를 주입받아 Dispatcher 생성
    @Bean
    public NotificationEngine notificationEngine(List<NotificationSender> senders) {
        return new NotificationDispatcher(senders);
    }
}