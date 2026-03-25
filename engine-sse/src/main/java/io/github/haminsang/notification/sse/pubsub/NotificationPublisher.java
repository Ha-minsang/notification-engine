package io.github.haminsang.notification.sse.pubsub;

import org.springframework.data.redis.core.RedisTemplate;

public class NotificationPublisher {

    private static final String CHANNEL = "notification";

    private final RedisTemplate<String, NotificationMessage> redisTemplate;

    public NotificationPublisher(RedisTemplate<String, NotificationMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publish(NotificationMessage message) {
        redisTemplate.convertAndSend(CHANNEL, message);
    }
}