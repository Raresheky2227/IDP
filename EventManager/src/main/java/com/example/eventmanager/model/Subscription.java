package com.example.eventmanager.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subscriptions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class Subscription implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "event_id")
    private String eventId;

    public Subscription() {}

    public Subscription(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public void setId(Long id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}
