package com.example.eventmanager.controller;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.service.EventService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    // Use a project-root-relative directory for uploads (can be changed to a Docker volume)
    public static final String BASE_UPLOAD_DIR = "uploads/pdfs";

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

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
        log.info("Listing all events");
        return eventService.getAllEvents();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addEvent(
            @RequestPart("event") Event event,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Use an absolute path relative to the project root
        Path uploadDir = Paths.get(BASE_UPLOAD_DIR).toAbsolutePath();
        log.info("Resolved uploads directory: {}", uploadDir);

        try {
            Files.createDirectories(uploadDir);
            log.info("Ensured upload directory exists: {}", uploadDir);
        } catch (IOException ex) {
            log.error("Could not create uploads/pdfs directory at " + uploadDir, ex);
            return ResponseEntity.status(500).body("Failed to create uploads directory");
        }

        if (jwt == null) {
            log.warn("Unauthorized attempt to add event.");
            return ResponseEntity.status(401).body("Unauthorized: JWT required.");
        }
        String username = jwt.getSubject();
        if (!isVipUser(jwt)) {
            log.warn("User '{}' attempted to add an event but is not a VIP!", username);
            return ResponseEntity.status(403).body("Only VIP users can create events.");
        }

        // Save PDF file to uploads/pdfs/
        String pdfFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destPath = uploadDir.resolve(pdfFilename);
        log.info("Saving uploaded PDF to: {}", destPath);
        try {
            file.transferTo(destPath.toFile());
            log.info("Successfully saved PDF: {}", destPath);
        } catch (IOException e) {
            log.error("Failed to save uploaded PDF to " + destPath, e);
            return ResponseEntity.status(500).body("Failed to save PDF file");
        }

        event.setPdfPath(pdfFilename);

        log.info("VIP User '{}' is adding event '{}'", username, event.getTitle());
        Event saved = eventService.addEvent(event);
        log.info("Event added: {}", saved.getId());
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
    public ResponseEntity<Resource> getPdf(@PathVariable String id) {
        return eventService.getEventById(id)
                .<ResponseEntity<Resource>>map((Event e) -> {
                    Path pdfPath = Paths.get(BASE_UPLOAD_DIR).toAbsolutePath().resolve(e.getPdfPath());
                    Resource pdf = new FileSystemResource(pdfPath);
                    log.info("Attempting to serve PDF at: {}", pdfPath);
                    if (!pdf.exists()) {
                        log.warn("Requested PDF not found: {}", pdfPath);
                        return ResponseEntity.<Resource>notFound().build();
                    }
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + e.getPdfPath() + "\"")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElseGet(() -> {
                    log.warn("Event not found for PDF download with id={}", id);
                    return ResponseEntity.<Resource>notFound().build();
                });
    }
}
