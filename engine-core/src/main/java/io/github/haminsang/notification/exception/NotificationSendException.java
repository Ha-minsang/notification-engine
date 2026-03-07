package io.github.haminsang.notification.exception;

import io.github.haminsang.notification.channel.NotificationChannel;

public class NotificationSendException extends NotificationException {

    private final NotificationChannel channel;

    public NotificationSendException(String message, NotificationChannel channel) {
        super(message);
        this.channel = channel;
    }

    public NotificationSendException(String message, NotificationChannel channel, Throwable cause) {
        super(message, cause);
        this.channel = channel;
    }

    public NotificationChannel getChannel() {
        return channel;
    }
}
