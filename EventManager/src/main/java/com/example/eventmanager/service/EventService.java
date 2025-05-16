package com.example.eventmanager.service;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Subscription;
import com.example.eventmanager.repository.EventRepository;
import com.example.eventmanager.repository.SubscriptionRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class EventService {
    private List<Event> events = new ArrayList<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EventRepository eventRepository;

    @PostConstruct
    public void init() throws IOException {
        // Load events.json only if DB is empty (first run)
        if (eventRepository.count() == 0) {
            List<Event> loadedEvents = objectMapper.readValue(
                    new ClassPathResource("events.json").getInputStream(),
                    new TypeReference<List<Event>>() {}
            );
            eventRepository.saveAll(loadedEvents);
        }
        events = eventRepository.findAll();
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(Long.valueOf(id));
    }

    public void subscribe(String userId, String eventId) {
        if (subscriptionRepository.findByUserIdAndEventId(userId, eventId).isEmpty()) {
            Subscription sub = new Subscription(userId, eventId);
            subscriptionRepository.save(sub);

            JSONObject event = new JSONObject();
            event.put("type", "event_subscription");
            event.put("recipient", userId);
            event.put("details", eventId);
            amqpTemplate.convertAndSend("notification-queue", event.toString());
        }
    }

    public Set<String> getSubscriptions(String userId) {
        List<Subscription> subs = subscriptionRepository.findByUserId(userId);
        Set<String> eventIds = new HashSet<>();
        for (Subscription s : subs) {
            eventIds.add(s.getEventId());
        }
        return eventIds;
    }

    @Transactional
    public void unsubscribe(String userId, String eventId) {
        subscriptionRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(String eventId) {
        subscriptionRepository.deleteByEventId(eventId);
        eventRepository.deleteById(Long.valueOf(eventId));
    }
}
