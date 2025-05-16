package com.example.eventmanager.service;

import com.example.eventmanager.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
public class EventService {
    private List<Event> events = new ArrayList<>();
    private Map<String, Set<String>> subscriptions = new HashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostConstruct
    public void init() throws IOException {
        events = objectMapper.readValue(
                new ClassPathResource("events.json").getInputStream(),
                new TypeReference<List<Event>>() {}
        );
    }

    public List<Event> getAllEvents() {
        return events;
    }

    public Optional<Event> getEventById(String id) {
        return events.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public void subscribe(String userId, String eventId) {
        subscriptions.computeIfAbsent(userId, k -> new HashSet<>()).add(eventId);

        // Send notification event to RabbitMQ
        JSONObject event = new JSONObject();
        event.put("type", "event_subscription");
        event.put("recipient", userId);
        event.put("details", eventId);
        amqpTemplate.convertAndSend("notification-queue", event.toString());
    }

    public Set<String> getSubscriptions(String userId) {
        return subscriptions.getOrDefault(userId, Collections.emptySet());
    }
}
