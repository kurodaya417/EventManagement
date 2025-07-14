package com.eventmanagement.mapper;

import com.eventmanagement.entity.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EventMapper Unit Tests
 * 
 * Tests for the EventMapper interface to validate DB connection and operations.
 * Uses H2 in-memory database for testing isolation.
 */
@MybatisTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class EventMapperTest {

    @Autowired
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        // Test data is loaded via schema.sql
    }

    @Test
    public void testDatabaseConnection() {
        // Test basic connection by counting all events
        int count = eventMapper.countAll();
        assertThat(count).isEqualTo(3); // 3 test events from schema.sql
    }

    @Test
    public void testFindAll() {
        // Test finding all events
        List<Event> events = eventMapper.findAll();
        
        assertThat(events).isNotNull();
        assertThat(events).hasSize(3);
        assertThat(events.get(0).getEventName()).isEqualTo("Test Event 3"); // Ordered by created_at DESC
    }

    @Test
    public void testFindById() {
        // Test finding event by ID
        Event event = eventMapper.findById(1L);
        
        assertThat(event).isNotNull();
        assertThat(event.getEventId()).isEqualTo(1L);
        assertThat(event.getEventName()).isEqualTo("Test Event 1");
        assertThat(event.getOrganizer()).isEqualTo("Test Organizer 1");
        assertThat(event.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    public void testFindByIdNotFound() {
        // Test finding non-existent event
        Event event = eventMapper.findById(999L);
        
        assertThat(event).isNull();
    }

    @Test
    public void testFindByStatus() {
        // Test finding events by status
        List<Event> activeEvents = eventMapper.findByStatus("ACTIVE");
        List<Event> completedEvents = eventMapper.findByStatus("COMPLETED");
        List<Event> cancelledEvents = eventMapper.findByStatus("CANCELLED");
        
        assertThat(activeEvents).hasSize(1);
        assertThat(activeEvents.get(0).getEventName()).isEqualTo("Test Event 1");
        
        assertThat(completedEvents).hasSize(1);
        assertThat(completedEvents.get(0).getEventName()).isEqualTo("Test Event 2");
        
        assertThat(cancelledEvents).hasSize(1);
        assertThat(cancelledEvents.get(0).getEventName()).isEqualTo("Test Event 3");
    }

    @Test
    public void testFindByOrganizer() {
        // Test finding events by organizer
        List<Event> events = eventMapper.findByOrganizer("Test Organizer 1");
        
        assertThat(events).hasSize(2); // Test Event 1 and Test Event 3
        assertThat(events.get(0).getEventName()).isIn("Test Event 1", "Test Event 3");
        assertThat(events.get(1).getEventName()).isIn("Test Event 1", "Test Event 3");
    }

    @Test
    public void testInsert() {
        // Test inserting new event
        Event newEvent = new Event();
        newEvent.setEventName("New Test Event");
        newEvent.setDescription("New Test Description");
        newEvent.setStartDateTime(LocalDateTime.of(2025, 1, 15, 10, 0));
        newEvent.setEndDateTime(LocalDateTime.of(2025, 1, 15, 12, 0));
        newEvent.setLocation("New Test Location");
        newEvent.setOrganizer("New Test Organizer");
        newEvent.setMaxParticipants(100);
        newEvent.setCurrentParticipants(0);
        newEvent.setStatus("ACTIVE");
        
        int result = eventMapper.insert(newEvent);
        
        assertThat(result).isEqualTo(1);
        assertThat(newEvent.getEventId()).isNotNull();
        
        // Verify the event was inserted
        Event insertedEvent = eventMapper.findById(newEvent.getEventId());
        assertThat(insertedEvent).isNotNull();
        assertThat(insertedEvent.getEventName()).isEqualTo("New Test Event");
        assertThat(insertedEvent.getOrganizer()).isEqualTo("New Test Organizer");
    }

    @Test
    public void testUpdate() {
        // Test updating existing event
        Event event = eventMapper.findById(1L);
        assertThat(event).isNotNull();
        
        event.setEventName("Updated Test Event");
        event.setDescription("Updated Test Description");
        event.setMaxParticipants(75);
        
        int result = eventMapper.update(event);
        
        assertThat(result).isEqualTo(1);
        
        // Verify the event was updated
        Event updatedEvent = eventMapper.findById(1L);
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getEventName()).isEqualTo("Updated Test Event");
        assertThat(updatedEvent.getDescription()).isEqualTo("Updated Test Description");
        assertThat(updatedEvent.getMaxParticipants()).isEqualTo(75);
    }

    @Test
    public void testDeleteById() {
        // Test deleting event by ID
        int result = eventMapper.deleteById(1L);
        
        assertThat(result).isEqualTo(1);
        
        // Verify the event was deleted
        Event deletedEvent = eventMapper.findById(1L);
        assertThat(deletedEvent).isNull();
        
        // Verify total count decreased
        int count = eventMapper.countAll();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testCountAll() {
        // Test counting all events
        int count = eventMapper.countAll();
        
        assertThat(count).isEqualTo(3);
    }

    @Test
    public void testCountByStatus() {
        // Test counting events by status
        int activeCount = eventMapper.countByStatus("ACTIVE");
        int completedCount = eventMapper.countByStatus("COMPLETED");
        int cancelledCount = eventMapper.countByStatus("CANCELLED");
        
        assertThat(activeCount).isEqualTo(1);
        assertThat(completedCount).isEqualTo(1);
        assertThat(cancelledCount).isEqualTo(1);
    }

    @Test
    public void testCountByStatusNotFound() {
        // Test counting events by non-existent status
        int count = eventMapper.countByStatus("NONEXISTENT");
        
        assertThat(count).isEqualTo(0);
    }
}