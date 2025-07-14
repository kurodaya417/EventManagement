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
 * Integration tests for EventService with Oracle database layer.
 * Tests the service layer operations with actual Oracle database connections.
 */
@MybatisTest
@Import(EventService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        // Tests use actual Oracle database
    }

    @Test
    public void testDatabaseIntegration() {
        // Test basic database integration
        List<EventResponse> events = eventService.getAllEvents();
        
        assertThat(events).isNotNull();
        // Don't assert specific size since it depends on actual database content
    }

    @Test
    public void testGetAllEvents() {
        // Test getting all events through service layer
        List<EventResponse> events = eventService.getAllEvents();
        
        assertThat(events).isNotNull();
        // Verify service layer properly returns data
        if (!events.isEmpty()) {
            EventResponse firstEvent = events.get(0);
            assertThat(firstEvent.getEventId()).isNotNull();
            assertThat(firstEvent.getEventName()).isNotNull();
            assertThat(firstEvent.getOrganizer()).isNotNull();
            assertThat(firstEvent.getStatus()).isNotNull();
        }
    }

    @Test
    public void testGetEventById() {
        // Test getting event by ID through service layer
        // First create a test event
        EventRequest request = createTestEventRequest();
        EventResponse createdEvent = eventService.createEvent(request);
        
        if (createdEvent != null) {
            EventResponse event = eventService.getEventById(createdEvent.getEventId());
            
            assertThat(event).isNotNull();
            assertThat(event.getEventId()).isEqualTo(createdEvent.getEventId());
            assertThat(event.getEventName()).isEqualTo(request.getEventName());
            assertThat(event.getOrganizer()).isEqualTo(request.getOrganizer());
            assertThat(event.getStatus()).isEqualTo("ACTIVE"); // Default status
            assertThat(event.getMaxParticipants()).isEqualTo(request.getMaxParticipants());
        }
    }

    @Test
    public void testGetEventByIdNotFound() {
        // Test getting non-existent event using a very large ID
        EventResponse event = eventService.getEventById(999999999L);
        
        assertThat(event).isNull();
    }

    @Test
    public void testGetEventsByStatus() {
        // Test getting events by status through service layer
        List<EventResponse> activeEvents = eventService.getEventsByStatus("ACTIVE");
        List<EventResponse> completedEvents = eventService.getEventsByStatus("COMPLETED");
        List<EventResponse> cancelledEvents = eventService.getEventsByStatus("CANCELLED");
        
        assertThat(activeEvents).isNotNull();
        assertThat(completedEvents).isNotNull();
        assertThat(cancelledEvents).isNotNull();
        
        // Verify service layer properly filters by status
        activeEvents.forEach(event -> assertThat(event.getStatus()).isEqualTo("ACTIVE"));
        completedEvents.forEach(event -> assertThat(event.getStatus()).isEqualTo("COMPLETED"));
        cancelledEvents.forEach(event -> assertThat(event.getStatus()).isEqualTo("CANCELLED"));
    }

    @Test
    public void testGetEventsByOrganizer() {
        // Test getting events by organizer through service layer
        // First create test events with same organizer
        String testOrganizer = "Test Organizer " + System.currentTimeMillis();
        EventRequest request1 = createTestEventRequest();
        EventRequest request2 = createTestEventRequest();
        request1.setOrganizer(testOrganizer);
        request2.setOrganizer(testOrganizer);
        
        EventResponse event1 = eventService.createEvent(request1);
        EventResponse event2 = eventService.createEvent(request2);
        
        if (event1 != null && event2 != null) {
            List<EventResponse> events = eventService.getEventsByOrganizer(testOrganizer);
            
            assertThat(events).isNotNull();
            assertThat(events).hasSize(2);
            assertThat(events).extracting("organizer").containsOnly(testOrganizer);
        }
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
        // First create a test event
        EventRequest createRequest = createTestEventRequest();
        EventResponse createdEvent = eventService.createEvent(createRequest);
        
        if (createdEvent != null) {
            EventRequest updateRequest = new EventRequest(
                "Updated Service Event",
                "Updated Service Description",
                LocalDateTime.of(2030, 3, 1, 10, 0),
                LocalDateTime.of(2030, 3, 1, 12, 0),
                "Updated Service Location",
                "Updated Service Organizer",
                75
            );
            
            EventResponse response = eventService.updateEvent(createdEvent.getEventId(), updateRequest);
            
            assertThat(response).isNotNull();
            assertThat(response.getEventId()).isEqualTo(createdEvent.getEventId());
            assertThat(response.getEventName()).isEqualTo("Updated Service Event");
            assertThat(response.getOrganizer()).isEqualTo("Updated Service Organizer");
            assertThat(response.getMaxParticipants()).isEqualTo(75);
            
            // Verify the event was updated in database
            Event updatedEvent = eventMapper.findById(createdEvent.getEventId());
            assertThat(updatedEvent).isNotNull();
            assertThat(updatedEvent.getEventName()).isEqualTo("Updated Service Event");
            assertThat(updatedEvent.getOrganizer()).isEqualTo("Updated Service Organizer");
        }
    }

    @Test
    public void testUpdateEventNotFound() {
        // Test updating non-existent event using a very large ID
        EventRequest request = new EventRequest(
            "Non-existent Event",
            "Non-existent Description",
            LocalDateTime.of(2030, 3, 1, 10, 0),
            LocalDateTime.of(2030, 3, 1, 12, 0),
            "Non-existent Location",
            "Non-existent Organizer",
            75
        );
        
        EventResponse response = eventService.updateEvent(999999999L, request);
        
        assertThat(response).isNull();
    }

    @Test
    public void testDeleteEvent() {
        // Test deleting event through service layer
        // First create a test event
        EventRequest createRequest = createTestEventRequest();
        EventResponse createdEvent = eventService.createEvent(createRequest);
        
        if (createdEvent != null) {
            boolean result = eventService.deleteEvent(createdEvent.getEventId());
            
            assertThat(result).isTrue();
            
            // Verify the event was deleted from database
            Event deletedEvent = eventMapper.findById(createdEvent.getEventId());
            assertThat(deletedEvent).isNull();
            
            // Verify through service layer
            EventResponse deletedResponse = eventService.getEventById(createdEvent.getEventId());
            assertThat(deletedResponse).isNull();
        }
    }

    @Test
    public void testDeleteEventNotFound() {
        // Test deleting non-existent event using a very large ID
        boolean result = eventService.deleteEvent(999999999L);
        
        assertThat(result).isFalse();
    }

    @Test
    public void testGetEventStatistics() {
        // Test getting event statistics through service layer
        EventService.EventStatistics stats = eventService.getEventStatistics();
        
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalEvents()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getActiveEvents()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getCompletedEvents()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getCancelledEvents()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testTransactionRollback() {
        // Test transaction rollback behavior
        int initialCount = eventMapper.countAll();
        
        // This test demonstrates that each test method runs in its own transaction
        // and automatically rolls back after the test
        EventRequest request = createTestEventRequest();
        request.setEventName("Rollback Test Event");
        request.setDescription("Rollback Test Description");
        
        EventResponse response = eventService.createEvent(request);
        assertThat(response).isNotNull();
        
        // Verify count increased within the same transaction
        int countAfterCreate = eventMapper.countAll();
        assertThat(countAfterCreate).isEqualTo(initialCount + 1);
        
        // After this test method completes, the transaction will be rolled back
        // by @Transactional annotation, so the next test will see the original state
    }

    // Helper method to create test event request
    private EventRequest createTestEventRequest() {
        return new EventRequest(
            "Test Event " + System.currentTimeMillis(),
            "Test Description " + System.currentTimeMillis(),
            LocalDateTime.of(2030, 2, 1, 10, 0),
            LocalDateTime.of(2030, 2, 1, 12, 0),
            "Test Location " + System.currentTimeMillis(),
            "Test Organizer " + System.currentTimeMillis(),
            100
        );
    }
}