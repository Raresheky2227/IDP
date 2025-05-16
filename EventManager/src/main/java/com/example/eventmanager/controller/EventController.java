package com.example.eventmanager.controller;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.service.EventService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

    // Utility method to check for ROLE_VIP in JWT claims
    private boolean isVipUser(Jwt jwt) {
        if (jwt == null) return false;
        Object rolesObj = jwt.getClaim("roles");
        if (rolesObj instanceof List<?>) {
            List<?> roles = (List<?>) rolesObj;
            return roles.contains("ROLE_VIP");
        } else if (rolesObj instanceof String) {
            return "ROLE_VIP".equals(rolesObj);
        }
        return false;
    }

    @GetMapping
    public List<Event> listEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public ResponseEntity<?> addEvent(
            @RequestBody Event event,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            log.warn("Unauthorized attempt to add event.");
            return ResponseEntity.status(401).body("Unauthorized: JWT required.");
        }
        String username = jwt.getSubject();
        if (!isVipUser(jwt)) {
            log.warn("User '{}' attempted to add an event but is not a VIP!", username);
            return ResponseEntity.status(403).body("Only VIP users can create events.");
        }
        log.info("VIP User '{}' is adding event '{}'", username, event.getTitle());
        Event saved = eventService.addEvent(event);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            log.warn("Unauthorized attempt to delete event.");
            return ResponseEntity.status(401).body("Unauthorized: JWT required.");
        }
        String username = jwt.getSubject();
        if (!isVipUser(jwt)) {
            log.warn("User '{}' attempted to delete event '{}' but is not a VIP!", username, id);
            return ResponseEntity.status(403).body("Only VIP users can delete events.");
        }
        log.info("VIP User '{}' deleting event '{}'", username, id);
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    // --- CHANGED: Use JWT principal instead of username param ---
    @PostMapping("/{id}/subscribe")
    public ResponseEntity<?> subscribe(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            log.warn("Unauthorized attempt to subscribe.");
            return ResponseEntity.status(401).body("Unauthorized: JWT required.");
        }
        String username = jwt.getSubject();
        log.info("User '{}' subscribing to event '{}'", username, id);
        eventService.subscribe(username, id);
        return ResponseEntity.ok().build();
    }

    // --- CHANGED: Use DELETE and JWT principal ---
    @DeleteMapping("/{id}/unsubscribe")
    public ResponseEntity<?> unsubscribe(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            log.warn("Unauthorized attempt to unsubscribe.");
            return ResponseEntity.status(401).body("Unauthorized: JWT required.");
        }
        String username = jwt.getSubject();
        log.info("User '{}' unsubscribing from event '{}'", username, id);
        eventService.unsubscribe(username, id);
        return ResponseEntity.ok().build();
    }

    // List of user's subscriptions
    @GetMapping("/subscriptions")
    public ResponseEntity<Set<String>> getSubscriptions(
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            log.warn("Unauthorized attempt to fetch subscriptions.");
            return ResponseEntity.status(401).build();
        }
        String username = jwt.getSubject();
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
