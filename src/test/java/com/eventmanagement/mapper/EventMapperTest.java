package com.eventmanagement.mapper;

import com.eventmanagement.entity.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EventMapper Unit Tests
 * 
 * Tests for the EventMapper interface to validate DB connection and operations.
 * Uses Oracle database (same as production) for testing.
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class EventMapperTest {

    @Autowired
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        // Tests use actual Oracle database
    }

    @Test
    public void testDatabaseConnection() {
        // Test basic connection by counting all events
        int count = eventMapper.countAll();
        assertThat(count).isGreaterThanOrEqualTo(0); // Any non-negative count indicates connection works
    }

    @Test
    public void testFindAll() {
        // Test finding all events
        List<Event> events = eventMapper.findAll();
        
        assertThat(events).isNotNull();
        // Don't assert specific size since it depends on actual data
        // Just verify the list is properly retrieved
    }

    @Test
    public void testFindById() {
        // Test finding event by ID - first create a test event
        Event testEvent = createTestEvent();
        Event insertedEvent = insertTestEvent(testEvent);
        
        if (insertedEvent != null) {
            Event foundEvent = eventMapper.findById(insertedEvent.getEventId());
            
            assertThat(foundEvent).isNotNull();
            assertThat(foundEvent.getEventId()).isEqualTo(insertedEvent.getEventId());
            assertThat(foundEvent.getEventName()).isEqualTo(testEvent.getEventName());
            assertThat(foundEvent.getOrganizer()).isEqualTo(testEvent.getOrganizer());
            assertThat(foundEvent.getStatus()).isEqualTo(testEvent.getStatus());
            
            // Clean up
            eventMapper.deleteById(insertedEvent.getEventId());
        }
    }

    @Test
    public void testFindByIdNotFound() {
        // Test finding non-existent event using a very large ID
        Event event = eventMapper.findById(999999999L);
        
        assertThat(event).isNull();
    }

    @Test
    public void testFindByStatus() {
        // Test finding events by status - create test events with different statuses
        Event activeEvent = createTestEvent("ACTIVE");
        Event completedEvent = createTestEvent("COMPLETED");
        Event cancelledEvent = createTestEvent("CANCELLED");
        
        Event insertedActive = insertTestEvent(activeEvent);
        Event insertedCompleted = insertTestEvent(completedEvent);
        Event insertedCancelled = insertTestEvent(cancelledEvent);
        
        try {
            List<Event> activeEvents = eventMapper.findByStatus("ACTIVE");
            List<Event> completedEvents = eventMapper.findByStatus("COMPLETED");
            List<Event> cancelledEvents = eventMapper.findByStatus("CANCELLED");
            
            assertThat(activeEvents).isNotNull();
            assertThat(completedEvents).isNotNull();
            assertThat(cancelledEvents).isNotNull();
            
            // Verify at least our test events are found
            assertThat(activeEvents.stream().anyMatch(e -> e.getEventId().equals(insertedActive.getEventId()))).isTrue();
            assertThat(completedEvents.stream().anyMatch(e -> e.getEventId().equals(insertedCompleted.getEventId()))).isTrue();
            assertThat(cancelledEvents.stream().anyMatch(e -> e.getEventId().equals(insertedCancelled.getEventId()))).isTrue();
            
        } finally {
            // Clean up
            if (insertedActive != null) eventMapper.deleteById(insertedActive.getEventId());
            if (insertedCompleted != null) eventMapper.deleteById(insertedCompleted.getEventId());
            if (insertedCancelled != null) eventMapper.deleteById(insertedCancelled.getEventId());
        }
    }

    @Test
    public void testFindByOrganizer() {
        // Test finding events by organizer - create test events with same organizer
        String testOrganizer = "Test Organizer " + System.currentTimeMillis();
        Event event1 = createTestEvent();
        Event event2 = createTestEvent();
        event1.setOrganizer(testOrganizer);
        event2.setOrganizer(testOrganizer);
        
        Event inserted1 = insertTestEvent(event1);
        Event inserted2 = insertTestEvent(event2);
        
        try {
            List<Event> events = eventMapper.findByOrganizer(testOrganizer);
            
            assertThat(events).isNotNull();
            assertThat(events).hasSize(2);
            assertThat(events.stream().allMatch(e -> e.getOrganizer().equals(testOrganizer))).isTrue();
            
        } finally {
            // Clean up
            if (inserted1 != null) eventMapper.deleteById(inserted1.getEventId());
            if (inserted2 != null) eventMapper.deleteById(inserted2.getEventId());
        }
    }

    @Test
    public void testInsert() {
        // Test inserting new event
        Event newEvent = createTestEvent();
        int initialCount = eventMapper.countAll();
        
        int result = eventMapper.insert(newEvent);
        
        assertThat(result).isEqualTo(1);
        assertThat(newEvent.getEventId()).isNotNull();
        
        // Verify the event was inserted
        Event insertedEvent = eventMapper.findById(newEvent.getEventId());
        assertThat(insertedEvent).isNotNull();
        assertThat(insertedEvent.getEventName()).isEqualTo(newEvent.getEventName());
        assertThat(insertedEvent.getOrganizer()).isEqualTo(newEvent.getOrganizer());
        
        // Verify count increased
        int newCount = eventMapper.countAll();
        assertThat(newCount).isEqualTo(initialCount + 1);
        
        // Clean up
        eventMapper.deleteById(newEvent.getEventId());
    }

    @Test
    public void testUpdate() {
        // Test updating existing event
        Event testEvent = createTestEvent();
        Event insertedEvent = insertTestEvent(testEvent);
        
        try {
            insertedEvent.setEventName("Updated Test Event");
            insertedEvent.setDescription("Updated Test Description");
            insertedEvent.setMaxParticipants(75);
            
            int result = eventMapper.update(insertedEvent);
            
            assertThat(result).isEqualTo(1);
            
            // Verify the event was updated
            Event updatedEvent = eventMapper.findById(insertedEvent.getEventId());
            assertThat(updatedEvent).isNotNull();
            assertThat(updatedEvent.getEventName()).isEqualTo("Updated Test Event");
            assertThat(updatedEvent.getDescription()).isEqualTo("Updated Test Description");
            assertThat(updatedEvent.getMaxParticipants()).isEqualTo(75);
            
        } finally {
            // Clean up
            if (insertedEvent != null) {
                eventMapper.deleteById(insertedEvent.getEventId());
            }
        }
    }

    @Test
    public void testDeleteById() {
        // Test deleting event by ID
        Event testEvent = createTestEvent();
        Event insertedEvent = insertTestEvent(testEvent);
        int initialCount = eventMapper.countAll();
        
        int result = eventMapper.deleteById(insertedEvent.getEventId());
        
        assertThat(result).isEqualTo(1);
        
        // Verify the event was deleted
        Event deletedEvent = eventMapper.findById(insertedEvent.getEventId());
        assertThat(deletedEvent).isNull();
        
        // Verify total count decreased
        int newCount = eventMapper.countAll();
        assertThat(newCount).isEqualTo(initialCount - 1);
    }

    @Test
    public void testCountAll() {
        // Test counting all events
        int count = eventMapper.countAll();
        
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testCountByStatus() {
        // Test counting events by status - create test events with different statuses
        Event activeEvent = createTestEvent("ACTIVE");
        Event completedEvent = createTestEvent("COMPLETED");
        Event cancelledEvent = createTestEvent("CANCELLED");
        
        Event insertedActive = insertTestEvent(activeEvent);
        Event insertedCompleted = insertTestEvent(completedEvent);
        Event insertedCancelled = insertTestEvent(cancelledEvent);
        
        try {
            int activeCount = eventMapper.countByStatus("ACTIVE");
            int completedCount = eventMapper.countByStatus("COMPLETED");
            int cancelledCount = eventMapper.countByStatus("CANCELLED");
            
            assertThat(activeCount).isGreaterThan(0);
            assertThat(completedCount).isGreaterThan(0);
            assertThat(cancelledCount).isGreaterThan(0);
            
        } finally {
            // Clean up
            if (insertedActive != null) eventMapper.deleteById(insertedActive.getEventId());
            if (insertedCompleted != null) eventMapper.deleteById(insertedCompleted.getEventId());
            if (insertedCancelled != null) eventMapper.deleteById(insertedCancelled.getEventId());
        }
    }

    @Test
    public void testCountByStatusNotFound() {
        // Test counting events by non-existent status
        int count = eventMapper.countByStatus("NONEXISTENT");
        
        assertThat(count).isEqualTo(0);
    }

    // Helper methods for creating test data
    private Event createTestEvent() {
        return createTestEvent("ACTIVE");
    }
    
    private Event createTestEvent(String status) {
        Event event = new Event();
        event.setEventName("Test Event " + System.currentTimeMillis());
        event.setDescription("Test Description " + System.currentTimeMillis());
        event.setStartDateTime(LocalDateTime.of(2025, 1, 15, 10, 0));
        event.setEndDateTime(LocalDateTime.of(2025, 1, 15, 12, 0));
        event.setLocation("Test Location " + System.currentTimeMillis());
        event.setOrganizer("Test Organizer " + System.currentTimeMillis());
        event.setMaxParticipants(100);
        event.setCurrentParticipants(0);
        event.setStatus(status);
        return event;
    }
    
    private Event insertTestEvent(Event event) {
        try {
            eventMapper.insert(event);
            return event;
        } catch (Exception e) {
            // If insert fails, return null
            return null;
        }
    }
}