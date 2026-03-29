package io.github.haminsang.notification.slack.sender;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SlackNotificationSenderTest {

    private WebClient webClient;
    private SlackNotificationSender sender;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        sender = new SlackNotificationSender(webClient);
    }

    @Test
    void 발송_요청이_오면_Webhook으로_메시지가_전송된다() {
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        NotificationRequest request = NotificationRequest.builder()
                .targetId("https://hooks.slack.com/services/test")
                .channel(NotificationChannel.SLACK)
                .payload(NotificationPayload.builder()
                        .title("테스트")
                        .body("테스트 메시지")
                        .build())
                .build();

        NotificationResult result = sender.send(request).join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.SLACK);
    }

    @Test
    void Webhook_호출_실패시_실패_결과를_반환한다() {
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("Webhook 호출 실패")));

        NotificationRequest request = NotificationRequest.builder()
                .targetId("https://hooks.slack.com/services/test")
                .channel(NotificationChannel.SLACK)
                .payload(NotificationPayload.builder()
                        .title("테스트")
                        .body("테스트 메시지")
                        .build())
                .build();

        NotificationResult result = sender.send(request).join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("Webhook 호출 실패");
    }
}