package com.example.eventmanager.controller;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.service.EventService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> listEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public ResponseEntity<Event> addEvent(
            @RequestBody Event event,
            @RequestParam String username
    ) {
        // Any user can add an event
        log.info("User '{}' is adding event '{}'", username, event.getTitle());
        Event saved = eventService.addEvent(event);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<Void> subscribe(
            @PathVariable String id,
            @RequestParam String username
    ) {
        log.info("User '{}' subscribing to event '{}'", username, id);
        eventService.subscribe(username, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unsubscribe")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable String id,
            @RequestParam String username
    ) {
        log.info("User '{}' unsubscribing from event '{}'", username, id);
        eventService.unsubscribe(username, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<Set<String>> getSubscriptions(
            @RequestParam String username
    ) {
        log.info("User '{}' fetching subscriptions", username);
        Set<String> subs = eventService.getSubscriptions(username);
        return ResponseEntity.ok(subs);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getPdf(@PathVariable String id) throws IOException {
        return eventService.getEventById(id)
                .map(e -> {
                    Resource pdf = new ClassPathResource("static/pdfs/" + e.getPdfPath());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + e.getPdfPath() + "\"")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElseGet(() -> ResponseEntity.<Resource>notFound().build());
    }
}
