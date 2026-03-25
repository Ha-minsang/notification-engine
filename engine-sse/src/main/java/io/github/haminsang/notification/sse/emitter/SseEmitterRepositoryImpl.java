package io.github.haminsang.notification.sse.emitter;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SseEmitterRepositoryImpl implements SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(String targetId, SseEmitter emitter) {
        emitters.put(targetId, emitter);
    }

    @Override
    public Optional<SseEmitter> find(String targetId) {
        return Optional.ofNullable(emitters.get(targetId));
    }

    @Override
    public void delete(String targetId) {
        emitters.remove(targetId);
    }
}