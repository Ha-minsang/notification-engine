package io.github.haminsang.notification.email.config;

import io.github.haminsang.notification.email.sender.EmailNotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailNotificationAutoConfiguration {

    @Value("${notification.email.from}")
    private String fromAddress;

    @Bean
    public EmailNotificationSender emailNotificationSender(JavaMailSender mailSender) {
        return new EmailNotificationSender(mailSender, fromAddress);
    }
}