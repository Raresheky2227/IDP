package com.raresheky.notification.listener;

import com.raresheky.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

@Component
public class EventListener {
    private final NotificationService notificationService;

    public EventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${notification.queue-name}")
    public void receiveEvent(String message) {
        // Expecting a JSON message: {"type":"signup", "recipient":"username", "event":"signup", "details":"..."}
        JSONObject event = new JSONObject(message);
        String recipient = event.optString("recipient", "unknown");
        String type = event.optString("type", "event");
        String details = event.optString("details", "");

        String notificationMessage;
        if ("signup".equals(type)) {
            notificationMessage = "Welcome, " + recipient + "! Thanks for signing up.";
        } else if ("event_subscription".equals(type)) {
            notificationMessage = recipient + " subscribed to event: " + details;
        } else {
            notificationMessage = "Event received: " + message;
        }

        notificationService.createNotification(recipient, notificationMessage);
    }
}
