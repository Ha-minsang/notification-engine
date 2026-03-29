package io.github.haminsang.notification.core;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import io.github.haminsang.notification.exception.NotificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NotificationDispatcherTest {

    private NotificationSender sseSender;
    private NotificationDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        sseSender = mock(NotificationSender.class);
        when(sseSender.supportedChannel()).thenReturn(NotificationChannel.SSE);
        dispatcher = new NotificationDispatcher(List.of(sseSender));
    }

    @Test
    void 채널에_맞는_Sender로_라우팅된다() {
        NotificationRequest request = NotificationRequest.builder()
                .targetId("user-123")
                .channel(NotificationChannel.SSE)
                .payload(NotificationPayload.builder()
                        .title("테스트")
                        .body("테스트 메시지")
                        .build())
                .build();

        when(sseSender.send(request))
                .thenReturn(CompletableFuture.completedFuture(
                        NotificationResult.success(NotificationChannel.SSE)));

        CompletableFuture<NotificationResult> result = dispatcher.send(request);

        assertThat(result.join().isSuccess()).isTrue();
        verify(sseSender, times(1)).send(request);
    }

    @Test
    void 지원하지_않는_채널이면_예외가_발생한다() {
        NotificationRequest request = NotificationRequest.builder()
                .targetId("user-123")
                .channel(NotificationChannel.EMAIL)
                .payload(NotificationPayload.builder()
                        .title("테스트")
                        .body("테스트 메시지")
                        .build())
                .build();

        assertThatThrownBy(() -> dispatcher.send(request))
                .isInstanceOf(NotificationException.class);
    }
}