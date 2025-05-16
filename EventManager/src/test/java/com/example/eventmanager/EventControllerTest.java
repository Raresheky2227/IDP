package com.example.eventmanager;

import com.example.eventmanager.controller.EventController;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @BeforeEach
    void setUp() {
        when(eventService.getAllEvents()).thenReturn(
                Collections.singletonList(new Event("1", "Test Event", "Desc", "event-1-details.pdf"))
        );
    }

    @Test
    public void listEvents_returnsOk() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    public void subscribe_returnsOk() throws Exception {
        mockMvc.perform(post("/api/events/1/subscribe"))
                .andExpect(status().isOk());
        verify(eventService).subscribe(anyString(), eq("1"));
    }
}