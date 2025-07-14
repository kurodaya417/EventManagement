package com.eventmanagement.controller;

import com.eventmanagement.dto.EventRequest;
import com.eventmanagement.dto.EventResponse;
import com.eventmanagement.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Event Controller Test
 */
@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllEvents() throws Exception {
        // Given
        List<EventResponse> mockEvents = Arrays.asList(
            new EventResponse(1L, "Test Event", "Description", 
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                "Location", "Organizer", 30, 10, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(eventService.getAllEvents()).thenReturn(mockEvents);

        // When & Then
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].eventId").value(1))
                .andExpect(jsonPath("$.data[0].eventName").value("Test Event"));
    }

    @Test
    public void testGetEventById() throws Exception {
        // Given
        EventResponse mockEvent = new EventResponse(1L, "Test Event", "Description",
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            "Location", "Organizer", 30, 10, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now());
        
        when(eventService.getEventById(1L)).thenReturn(mockEvent);

        // When & Then
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eventId").value(1))
                .andExpect(jsonPath("$.data.eventName").value("Test Event"));
    }

    @Test
    public void testCreateEvent() throws Exception {
        // Given
        EventRequest request = new EventRequest("New Event", "Description",
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            "Location", "Organizer", 30);
        
        EventResponse mockResponse = new EventResponse(1L, "New Event", "Description",
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            "Location", "Organizer", 30, 0, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now());
        
        when(eventService.createEvent(any(EventRequest.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eventName").value("New Event"));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        // Given
        EventRequest request = new EventRequest("Updated Event", "Updated Description",
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            "Location", "Organizer", 30);
        
        EventResponse mockResponse = new EventResponse(1L, "Updated Event", "Updated Description",
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            "Location", "Organizer", 30, 0, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now());
        
        when(eventService.updateEvent(eq(1L), any(EventRequest.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eventName").value("Updated Event"));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        // Given
        when(eventService.deleteEvent(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetEventStatistics() throws Exception {
        // Given
        EventService.EventStatistics mockStats = new EventService.EventStatistics(100, 45, 50, 5);
        when(eventService.getEventStatistics()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/events/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEvents").value(100))
                .andExpect(jsonPath("$.data.activeEvents").value(45));
    }
}