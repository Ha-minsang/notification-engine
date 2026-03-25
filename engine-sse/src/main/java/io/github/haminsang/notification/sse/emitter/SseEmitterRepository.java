package io.github.haminsang.notification.sse.emitter;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface SseEmitterRepository {

    // Emitter 저장
    void save(String targetId, SseEmitter emitter);

    // targetId로 Emitter 조회
    Optional<SseEmitter> find(String targetId);

    // Emitter 삭제
    void delete(String targetId);
}
