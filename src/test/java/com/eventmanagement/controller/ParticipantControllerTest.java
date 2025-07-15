package com.eventmanagement.controller;

import com.eventmanagement.dto.ParticipantRequest;
import com.eventmanagement.dto.ParticipantResponse;
import com.eventmanagement.service.ParticipantService;
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
 * Participant Controller Test
 */
@WebMvcTest(ParticipantController.class)
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantService participantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetParticipantsByEventId() throws Exception {
        // Given
        Long eventId = 1L;
        List<ParticipantResponse> mockParticipants = Arrays.asList(
            new ParticipantResponse(1L, eventId, "John Doe", "john@example.com", 
                "123-456-7890", LocalDateTime.now()),
            new ParticipantResponse(2L, eventId, "Jane Smith", "jane@example.com", 
                "098-765-4321", LocalDateTime.now())
        );
        
        when(participantService.getParticipantsByEventId(eventId)).thenReturn(mockParticipants);

        // When & Then
        mockMvc.perform(get("/participants/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Participants retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].participantName").value("John Doe"))
                .andExpect(jsonPath("$.data[0].participantEmail").value("john@example.com"))
                .andExpect(jsonPath("$.data[1].participantName").value("Jane Smith"))
                .andExpect(jsonPath("$.data[1].participantEmail").value("jane@example.com"));
    }

    @Test
    public void testGetEventsByParticipant() throws Exception {
        // Given
        String participantEmail = "john@example.com";
        List<ParticipantResponse> mockEvents = Arrays.asList(
            new ParticipantResponse(1L, 1L, "John Doe", participantEmail, 
                "123-456-7890", LocalDateTime.now()),
            new ParticipantResponse(2L, 2L, "John Doe", participantEmail, 
                "123-456-7890", LocalDateTime.now())
        );
        
        when(participantService.getEventsByParticipant(participantEmail)).thenReturn(mockEvents);

        // When & Then
        mockMvc.perform(get("/participants/participant/{participantEmail}", participantEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Events retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].eventId").value(1))
                .andExpect(jsonPath("$.data[1].eventId").value(2));
    }

    @Test
    public void testRegisterParticipant() throws Exception {
        // Given
        ParticipantRequest request = new ParticipantRequest(1L, "John Doe", "john@example.com", "123-456-7890");
        ParticipantResponse mockResponse = new ParticipantResponse(1L, 1L, "John Doe", "john@example.com", 
            "123-456-7890", LocalDateTime.now());
        
        when(participantService.registerParticipant(any(ParticipantRequest.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/participants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Participant registered successfully"))
                .andExpect(jsonPath("$.data.participantName").value("John Doe"))
                .andExpect(jsonPath("$.data.participantEmail").value("john@example.com"));
    }

    @Test
    public void testRegisterParticipantWithInvalidRequest() throws Exception {
        // Given - Invalid request with missing required fields
        ParticipantRequest request = new ParticipantRequest(null, "", "", "");

        // When & Then
        mockMvc.perform(post("/participants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterParticipantDuplicateRegistration() throws Exception {
        // Given
        ParticipantRequest request = new ParticipantRequest(1L, "John Doe", "john@example.com", "123-456-7890");
        
        when(participantService.registerParticipant(any(ParticipantRequest.class)))
                .thenThrow(new RuntimeException("Participant already registered for this event"));

        // When & Then
        mockMvc.perform(post("/participants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Registration failed: Participant already registered for this event"));
    }

    @Test
    public void testCancelParticipation() throws Exception {
        // Given
        Long participationId = 1L;
        when(participantService.cancelParticipation(participationId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/participants/cancel/{participationId}", participationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Participation cancelled successfully"));
    }

    @Test
    public void testCancelParticipationNotFound() throws Exception {
        // Given
        Long participationId = 999L;
        when(participantService.cancelParticipation(participationId))
                .thenThrow(new RuntimeException("Participation not found"));

        // When & Then
        mockMvc.perform(delete("/participants/cancel/{participationId}", participationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Participation not found: Participation not found"));
    }

    @Test
    public void testCancelParticipationByEventAndEmail() throws Exception {
        // Given
        Long eventId = 1L;
        String participantEmail = "john@example.com";
        when(participantService.cancelParticipationByEventAndEmail(eventId, participantEmail)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/participants/cancel/event/{eventId}/email/{participantEmail}", eventId, participantEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Participation cancelled successfully"));
    }

    @Test
    public void testGetParticipantsByEventIdNotFound() throws Exception {
        // Given
        Long eventId = 999L;
        when(participantService.getParticipantsByEventId(eventId))
                .thenThrow(new RuntimeException("Event not found with ID: " + eventId));

        // When & Then
        mockMvc.perform(get("/participants/event/{eventId}", eventId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Event not found: Event not found with ID: " + eventId));
    }
}