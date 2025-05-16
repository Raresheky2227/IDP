package com.raresheky.notification.controller;

import com.raresheky.notification.model.Notification;
import com.raresheky.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // List notifications for recipient
    @GetMapping("/{recipient}")
    public List<Notification> getForUser(@PathVariable String recipient) {
        return service.getNotifications(recipient);
    }

    // Mark as read
    @PostMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        service.markAsRead(id);
    }

    // Manual trigger for testing
    @PostMapping("/manual")
    public Notification create(
            @RequestParam String recipient,
            @RequestParam String message) {
        return service.createNotification(recipient, message);
    }
}
