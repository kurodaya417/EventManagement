package com.eventmanagement.controller;

import com.eventmanagement.dto.EventSearchRequest;
import com.eventmanagement.dto.EventSearchResult;
import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for EventController search functionality
 */
@WebMvcTest(EventController.class)
class EventControllerSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventSearchRequest searchRequest;
    private EventSearchResult searchResult;

    @BeforeEach
    void setUp() {
        searchRequest = new EventSearchRequest();
        searchRequest.setKeyword("Spring");
        searchRequest.setStatus("ACTIVE");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("createdAt");
        searchRequest.setSortOrder("desc");

        searchResult = new EventSearchResult(Collections.emptyList(), 0, 0, 10);
    }

    @Test
    void testSearchEventsSuccess() throws Exception {
        // Given
        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Search completed successfully"))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void testSearchEventsWithKeywordOnly() throws Exception {
        // Given
        EventSearchRequest keywordRequest = new EventSearchRequest();
        keywordRequest.setKeyword("Workshop");
        keywordRequest.setPage(0);
        keywordRequest.setSize(5);

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(keywordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchEventsWithDateRange() throws Exception {
        // Given
        EventSearchRequest dateRangeRequest = new EventSearchRequest();
        dateRangeRequest.setStartDateFrom(LocalDateTime.of(2024, 1, 1, 0, 0));
        dateRangeRequest.setStartDateTo(LocalDateTime.of(2024, 12, 31, 23, 59));
        dateRangeRequest.setPage(0);
        dateRangeRequest.setSize(10);

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dateRangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchEventsWithInvalidSortField() throws Exception {
        // Given
        EventSearchRequest invalidRequest = new EventSearchRequest();
        invalidRequest.setKeyword("Test");
        invalidRequest.setSortBy("invalidField");

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid sort field: invalidField"));

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid search criteria: Invalid sort field: invalidField"));
    }

    @Test
    void testSearchEventsWithInvalidPageSize() throws Exception {
        // Given
        EventSearchRequest invalidRequest = new EventSearchRequest();
        invalidRequest.setKeyword("Test");
        invalidRequest.setSize(0); // Invalid page size - this will trigger validation error

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Spring validation will return 400 Bad Request
    }

    @Test
    void testSearchEventsWithLocationFilter() throws Exception {
        // Given
        EventSearchRequest locationRequest = new EventSearchRequest();
        locationRequest.setLocation("Conference Room");
        locationRequest.setPage(0);
        locationRequest.setSize(10);

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchEventsWithOrganizerFilter() throws Exception {
        // Given
        EventSearchRequest organizerRequest = new EventSearchRequest();
        organizerRequest.setOrganizer("John Doe");
        organizerRequest.setPage(0);
        organizerRequest.setSize(10);

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchEventsWithSortingAsc() throws Exception {
        // Given
        EventSearchRequest sortRequest = new EventSearchRequest();
        sortRequest.setKeyword("Test");
        sortRequest.setSortBy("eventName");
        sortRequest.setSortOrder("asc");

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sortRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchEventsServiceException() throws Exception {
        // Given
        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to search events: Database connection failed"));
    }

    @Test
    void testSearchEventsWithEmptyRequest() throws Exception {
        // Given
        EventSearchRequest emptyRequest = new EventSearchRequest();

        when(eventService.searchEvents(any(EventSearchRequest.class)))
                .thenReturn(searchResult);

        // When & Then
        mockMvc.perform(post("/events/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}