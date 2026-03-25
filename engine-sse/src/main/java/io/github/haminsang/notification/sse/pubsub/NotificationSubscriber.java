package io.github.haminsang.notification.sse.pubsub;

import io.github.haminsang.notification.sse.emitter.SseEmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
public class NotificationSubscriber implements MessageListener {

    private final SseEmitterRepository emitterRepository;
    private final JacksonJsonRedisSerializer<NotificationMessage> serializer;

    public NotificationSubscriber(SseEmitterRepository emitterRepository,
                                  JacksonJsonRedisSerializer<NotificationMessage> serializer) {
        this.emitterRepository = emitterRepository;
        this.serializer = serializer;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            NotificationMessage notificationMessage = serializer.deserialize(message.getBody());
            if (notificationMessage == null) {
                log.warn("역직렬화 결과가 null");
                return;
            }
            emitterRepository.find(notificationMessage.getTargetId())
                    .ifPresent(emitter -> send(emitter, notificationMessage));
        } catch (Exception e) {
            log.error("메시지 역직렬화 실패", e);
        }
    }

    private void send(SseEmitter emitter, NotificationMessage message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message.getPayload()));
        } catch (IOException e) {
            log.error("SSE 전송 실패: targetId={}", message.getTargetId(), e);
            emitterRepository.delete(message.getTargetId());
        }
    }
}