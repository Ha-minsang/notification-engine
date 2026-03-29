package io.github.haminsang.notification.email.sender;

import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailNotificationSenderTest {

    private JavaMailSender mailSender;
    private EmailNotificationSender sender;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        sender = new EmailNotificationSender(mailSender, "from@test.com");
    }

    @Test
    void 발송_요청이_오면_메일이_전송된다() {
        NotificationRequest request = NotificationRequest.builder()
                .targetId("to@test.com")
                .channel(NotificationChannel.EMAIL)
                .payload(NotificationPayload.builder()
                        .title("테스트 제목")
                        .body("테스트 본문")
                        .build())
                .build();

        NotificationResult result = sender.send(request).join();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void 메일_전송_실패시_실패_결과를_반환한다() {
        NotificationRequest request = NotificationRequest.builder()
                .targetId("to@test.com")
                .channel(NotificationChannel.EMAIL)
                .payload(NotificationPayload.builder()
                        .title("테스트 제목")
                        .body("테스트 본문")
                        .build())
                .build();

        doThrow(new RuntimeException("SMTP 연결 실패"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        NotificationResult result = sender.send(request).join();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("SMTP 연결 실패");
    }
}