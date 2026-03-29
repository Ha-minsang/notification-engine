package io.github.haminsang.notification.slack;

import io.github.haminsang.notification.slack.sender.SlackNotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SlackNotificationAutoConfiguration {

    @Bean
    public WebClient slackWebClient() {
        return WebClient.builder()
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public SlackNotificationSender slackNotificationSender(WebClient slackWebClient) {
        return new SlackNotificationSender(slackWebClient);
    }
}