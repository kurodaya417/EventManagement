package com.eventmanagement.service;

import com.eventmanagement.dto.EventRequest;
import com.eventmanagement.dto.EventResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * EventService Integration Tests
 * 
 * Integration tests for EventService with database layer.
 * Tests the service layer operations with actual database connections.
 */
@MybatisTest
@Import(EventService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        // Test data is loaded via schema.sql
    }

    @Test
    public void testDatabaseIntegration() {
        // Test basic database integration
        List<EventResponse> events = eventService.getAllEvents();
        
        assertThat(events).isNotNull();
        assertThat(events).hasSize(3);
    }

    @Test
    public void testGetAllEvents() {
        // Test getting all events through service layer
        List<EventResponse> events = eventService.getAllEvents();
        
        assertThat(events).isNotNull();
        assertThat(events).hasSize(3);
        
        // Verify events are sorted by created_at DESC
        EventResponse firstEvent = events.get(0);
        assertThat(firstEvent.getEventName()).isEqualTo("Test Event 3");
        
        // Verify data conversion from entity to response
        assertThat(firstEvent.getEventId()).isNotNull();
        assertThat(firstEvent.getOrganizer()).isEqualTo("Test Organizer 1");
        assertThat(firstEvent.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    public void testGetEventById() {
        // Test getting event by ID through service layer
        EventResponse event = eventService.getEventById(1L);
        
        assertThat(event).isNotNull();
        assertThat(event.getEventId()).isEqualTo(1L);
        assertThat(event.getEventName()).isEqualTo("Test Event 1");
        assertThat(event.getOrganizer()).isEqualTo("Test Organizer 1");
        assertThat(event.getStatus()).isEqualTo("ACTIVE");
        assertThat(event.getMaxParticipants()).isEqualTo(50);
        assertThat(event.getCurrentParticipants()).isEqualTo(10);
    }

    @Test
    public void testGetEventByIdNotFound() {
        // Test getting non-existent event
        EventResponse event = eventService.getEventById(999L);
        
        assertThat(event).isNull();
    }

    @Test
    public void testGetEventsByStatus() {
        // Test getting events by status through service layer
        List<EventResponse> activeEvents = eventService.getEventsByStatus("ACTIVE");
        List<EventResponse> completedEvents = eventService.getEventsByStatus("COMPLETED");
        List<EventResponse> cancelledEvents = eventService.getEventsByStatus("CANCELLED");
        
        assertThat(activeEvents).hasSize(1);
        assertThat(activeEvents.get(0).getEventName()).isEqualTo("Test Event 1");
        
        assertThat(completedEvents).hasSize(1);
        assertThat(completedEvents.get(0).getEventName()).isEqualTo("Test Event 2");
        
        assertThat(cancelledEvents).hasSize(1);
        assertThat(cancelledEvents.get(0).getEventName()).isEqualTo("Test Event 3");
    }

    @Test
    public void testGetEventsByOrganizer() {
        // Test getting events by organizer through service layer
        List<EventResponse> events = eventService.getEventsByOrganizer("Test Organizer 1");
        
        assertThat(events).hasSize(2);
        assertThat(events).extracting("eventName").contains("Test Event 1", "Test Event 3");
    }

    @Test
    public void testCreateEvent() {
        // Test creating new event through service layer
        EventRequest request = new EventRequest(
            "New Service Event",
            "New Service Description",
            LocalDateTime.of(2030, 2, 1, 10, 0),
            LocalDateTime.of(2030, 2, 1, 12, 0),
            "New Service Location",
            "New Service Organizer",
            100
        );
        
        EventResponse response = eventService.createEvent(request);
        
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isNotNull();
        assertThat(response.getEventName()).isEqualTo("New Service Event");
        assertThat(response.getOrganizer()).isEqualTo("New Service Organizer");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getCurrentParticipants()).isEqualTo(0);
        
        // Verify the event was persisted in database
        Event persistedEvent = eventMapper.findById(response.getEventId());
        assertThat(persistedEvent).isNotNull();
        assertThat(persistedEvent.getEventName()).isEqualTo("New Service Event");
    }

    @Test
    public void testCreateEventValidationFailure() {
        // Test creating event with invalid data
        EventRequest request = new EventRequest(
            "Invalid Event",
            "Invalid Description",
            LocalDateTime.of(2030, 2, 1, 12, 0), // End time before start time
            LocalDateTime.of(2030, 2, 1, 10, 0),
            "Invalid Location",
            "Invalid Organizer",
            100
        );
        
        assertThatThrownBy(() -> eventService.createEvent(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Start date/time must be before end date/time");
    }

    @Test
    public void testCreateEventPastDateValidation() {
        // Test creating event with past date
        EventRequest request = new EventRequest(
            "Past Event",
            "Past Description",
            LocalDateTime.of(2020, 1, 1, 10, 0), // Past date
            LocalDateTime.of(2020, 1, 1, 12, 0),
            "Past Location",
            "Past Organizer",
            100
        );
        
        assertThatThrownBy(() -> eventService.createEvent(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Start date/time must be in the future");
    }

    @Test
    public void testUpdateEvent() {
        // Test updating existing event through service layer
        EventRequest request = new EventRequest(
            "Updated Service Event",
            "Updated Service Description",
            LocalDateTime.of(2030, 3, 1, 10, 0),
            LocalDateTime.of(2030, 3, 1, 12, 0),
            "Updated Service Location",
            "Updated Service Organizer",
            75
        );
        
        EventResponse response = eventService.updateEvent(1L, request);
        
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getEventName()).isEqualTo("Updated Service Event");
        assertThat(response.getOrganizer()).isEqualTo("Updated Service Organizer");
        assertThat(response.getMaxParticipants()).isEqualTo(75);
        
        // Verify the event was updated in database
        Event updatedEvent = eventMapper.findById(1L);
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getEventName()).isEqualTo("Updated Service Event");
        assertThat(updatedEvent.getOrganizer()).isEqualTo("Updated Service Organizer");
    }

    @Test
    public void testUpdateEventNotFound() {
        // Test updating non-existent event
        EventRequest request = new EventRequest(
            "Non-existent Event",
            "Non-existent Description",
            LocalDateTime.of(2030, 3, 1, 10, 0),
            LocalDateTime.of(2030, 3, 1, 12, 0),
            "Non-existent Location",
            "Non-existent Organizer",
            75
        );
        
        EventResponse response = eventService.updateEvent(999L, request);
        
        assertThat(response).isNull();
    }

    @Test
    public void testDeleteEvent() {
        // Test deleting event through service layer
        boolean result = eventService.deleteEvent(1L);
        
        assertThat(result).isTrue();
        
        // Verify the event was deleted from database
        Event deletedEvent = eventMapper.findById(1L);
        assertThat(deletedEvent).isNull();
        
        // Verify through service layer
        EventResponse deletedResponse = eventService.getEventById(1L);
        assertThat(deletedResponse).isNull();
    }

    @Test
    public void testDeleteEventNotFound() {
        // Test deleting non-existent event
        boolean result = eventService.deleteEvent(999L);
        
        assertThat(result).isFalse();
    }

    @Test
    public void testGetEventStatistics() {
        // Test getting event statistics through service layer
        EventService.EventStatistics stats = eventService.getEventStatistics();
        
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalEvents()).isEqualTo(3);
        assertThat(stats.getActiveEvents()).isEqualTo(1);
        assertThat(stats.getCompletedEvents()).isEqualTo(1);
        assertThat(stats.getCancelledEvents()).isEqualTo(1);
    }

    @Test
    public void testTransactionRollback() {
        // Test transaction rollback behavior
        int initialCount = eventMapper.countAll();
        
        // This test demonstrates that each test method runs in its own transaction
        // and automatically rolls back after the test
        EventRequest request = new EventRequest(
            "Rollback Test Event",
            "Rollback Test Description",
            LocalDateTime.of(2030, 4, 1, 10, 0),
            LocalDateTime.of(2030, 4, 1, 12, 0),
            "Rollback Test Location",
            "Rollback Test Organizer",
            100
        );
        
        EventResponse response = eventService.createEvent(request);
        assertThat(response).isNotNull();
        
        // Verify count increased within the same transaction
        int countAfterCreate = eventMapper.countAll();
        assertThat(countAfterCreate).isEqualTo(initialCount + 1);
        
        // After this test method completes, the transaction will be rolled back
        // by @Transactional annotation, so the next test will see the original state
    }
}