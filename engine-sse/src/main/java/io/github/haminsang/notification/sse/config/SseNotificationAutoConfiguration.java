package io.github.haminsang.notification.sse.config;

import io.github.haminsang.notification.sse.emitter.SseEmitterRepository;
import io.github.haminsang.notification.sse.emitter.SseEmitterRepositoryImpl;
import io.github.haminsang.notification.sse.pubsub.NotificationMessage;
import io.github.haminsang.notification.sse.pubsub.NotificationPublisher;
import io.github.haminsang.notification.sse.pubsub.NotificationSubscriber;
import io.github.haminsang.notification.sse.sender.SseNotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SseNotificationAutoConfiguration {

    private static final String CHANNEL = "notification";

    @Bean
    public JacksonJsonRedisSerializer<NotificationMessage> notificationMessageSerializer() {
        return new JacksonJsonRedisSerializer<>(NotificationMessage.class);
    }

    @Bean
    public RedisTemplate<String, NotificationMessage> redisTemplate(
            RedisConnectionFactory factory,
            JacksonJsonRedisSerializer<NotificationMessage> serializer) {
        RedisTemplate<String, NotificationMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean
    public SseEmitterRepository sseEmitterRepository() {
        return new SseEmitterRepositoryImpl();
    }

    @Bean
    public NotificationSubscriber notificationSubscriber(
            SseEmitterRepository repository,
            JacksonJsonRedisSerializer<NotificationMessage> serializer) {
        return new NotificationSubscriber(repository, serializer);
    }

    @Bean
    public NotificationPublisher notificationPublisher(
            RedisTemplate<String, NotificationMessage> redisTemplate) {
        return new NotificationPublisher(redisTemplate);
    }

    @Bean
    public SseNotificationSender sseNotificationSender(NotificationPublisher publisher) {
        return new SseNotificationSender(publisher);
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(
            RedisConnectionFactory factory,
            NotificationSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(subscriber, new PatternTopic(CHANNEL));
        return container;
    }
}