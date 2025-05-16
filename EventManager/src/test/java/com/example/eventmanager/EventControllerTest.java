package com.example.eventmanager;

import com.example.eventmanager.controller.EventController;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false) // Remove if you want to actually test security
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        when(eventService.getAllEvents()).thenReturn(
                Collections.singletonList(new Event("Test Event", "Desc", "event-1-details.pdf"))
        );
        jwt = new Jwt(
                "tokenvalue",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),                 // headers
                Map.of("sub", "testuser", "roles", "ROLE_USER") // claims
        );
    }

    @Test
    public void listEvents_returnsOk() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Event"));
    }

    @Test
    public void subscribe_returnsOk() throws Exception {
        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(jwt, Collections.emptyList());
        mockMvc.perform(post("/api/events/1/subscribe")
                        .param("username", "testuser")
                        .with(authentication(jwtAuth)))
                .andExpect(status().isOk());
        verify(eventService).subscribe(eq("testuser"), eq("1"));
    }
}
