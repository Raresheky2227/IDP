package com.raresheky.notification.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;
    private String message;
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Notification() {}
    public Notification(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
