package io.github.haminsang.notification.sse;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import io.github.haminsang.notification.sse.pubsub.NotificationMessage;
import io.github.haminsang.notification.sse.pubsub.NotificationPublisher;
import io.github.haminsang.notification.sse.sender.SseNotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SseNotificationSenderTest {

    private NotificationPublisher publisher;
    private SseNotificationSender sender;

    @BeforeEach
    void setUp() {
        publisher = mock(NotificationPublisher.class);
        sender = new SseNotificationSender(publisher);
    }

    @Test
    void 발송_요청이_오면_Publisher에_메시지가_전달된다() {
        NotificationRequest request = NotificationRequest.builder()
                .targetId("user-123")
                .channel(NotificationChannel.SSE)
                .payload(NotificationPayload.builder()
                        .title("테스트")
                        .body("테스트 메시지")
                        .build())
                .build();

        NotificationResult result = sender.send(request).join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.SSE);
        verify(publisher, times(1)).publish(any(NotificationMessage.class));
    }

    @Test
    void Publisher_예외_발생시_실패_결과를_반환한다() {
        NotificationRequest request = NotificationRequest.builder()
                .targetId("user-123")
                .channel(NotificationChannel.SSE)
                .payload(NotificationPayload.builder()
                        .title("테스트")
                        .body("테스트 메시지")
                        .build())
                .build();

        doThrow(new RuntimeException("Redis 연결 실패"))
                .when(publisher).publish(any(NotificationMessage.class));

        NotificationResult result = sender.send(request).join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("Redis 연결 실패");
    }
}