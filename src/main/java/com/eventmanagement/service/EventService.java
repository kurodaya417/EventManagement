package com.eventmanagement.service;

import com.eventmanagement.dto.EventRequest;
import com.eventmanagement.dto.EventResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event Service
 * 
 * Service layer for event management operations.
 */
@Service
@Transactional
public class EventService {

    @Autowired
    private EventMapper eventMapper;

    /**
     * Get all events
     * 
     * @return List of all events
     */
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventMapper.findAll();
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get event by ID
     * 
     * @param eventId Event ID
     * @return Event response or null if not found
     */
    public EventResponse getEventById(Long eventId) {
        Event event = eventMapper.findById(eventId);
        return event != null ? convertToResponse(event) : null;
    }

    /**
     * Get event entity by ID (for internal use)
     * 
     * @param eventId Event ID
     * @return Event entity or throws exception if not found
     */
    public Event getEventEntityById(Long eventId) {
        Event event = eventMapper.findById(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
        return event;
    }

    /**
     * Get events by status
     * 
     * @param status Event status
     * @return List of events with specified status
     */
    public List<EventResponse> getEventsByStatus(String status) {
        List<Event> events = eventMapper.findByStatus(status);
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get events by organizer
     * 
     * @param organizer Organizer name
     * @return List of events by organizer
     */
    public List<EventResponse> getEventsByOrganizer(String organizer) {
        List<Event> events = eventMapper.findByOrganizer(organizer);
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create new event
     * 
     * @param request Event request
     * @return Created event response
     */
    public EventResponse createEvent(EventRequest request) {
        validateEventRequest(request);
        
        Event event = new Event(
            request.getEventName(),
            request.getDescription(),
            request.getStartDateTime(),
            request.getEndDateTime(),
            request.getLocation(),
            request.getOrganizer(),
            request.getMaxParticipants()
        );
        
        eventMapper.insert(event);
        return convertToResponse(event);
    }

    /**
     * Update existing event
     * 
     * @param eventId Event ID
     * @param request Event request
     * @return Updated event response or null if not found
     */
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        validateEventRequest(request);
        
        Event existingEvent = eventMapper.findById(eventId);
        if (existingEvent == null) {
            return null;
        }
        
        existingEvent.setEventName(request.getEventName());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setStartDateTime(request.getStartDateTime());
        existingEvent.setEndDateTime(request.getEndDateTime());
        existingEvent.setLocation(request.getLocation());
        existingEvent.setOrganizer(request.getOrganizer());
        existingEvent.setMaxParticipants(request.getMaxParticipants());
        existingEvent.setUpdatedAt(LocalDateTime.now());
        
        eventMapper.update(existingEvent);
        return convertToResponse(existingEvent);
    }

    /**
     * Delete event by ID
     * 
     * @param eventId Event ID
     * @return true if deleted, false if not found
     */
    public boolean deleteEvent(Long eventId) {
        Event existingEvent = eventMapper.findById(eventId);
        if (existingEvent == null) {
            return false;
        }
        
        eventMapper.deleteById(eventId);
        return true;
    }

    /**
     * Get event statistics
     * 
     * @return Event statistics
     */
    public EventStatistics getEventStatistics() {
        int totalEvents = eventMapper.countAll();
        int activeEvents = eventMapper.countByStatus("ACTIVE");
        int completedEvents = eventMapper.countByStatus("COMPLETED");
        int cancelledEvents = eventMapper.countByStatus("CANCELLED");
        
        return new EventStatistics(totalEvents, activeEvents, completedEvents, cancelledEvents);
    }

    /**
     * Update participant count for an event
     * 
     * @param eventId Event ID
     * @param currentCount Current participant count
     */
    public void updateParticipantCount(Long eventId, int currentCount) {
        Event event = eventMapper.findById(eventId);
        if (event != null) {
            event.setCurrentParticipants(currentCount);
            event.setUpdatedAt(LocalDateTime.now());
            eventMapper.update(event);
        }
    }

    /**
     * Convert Event entity to EventResponse DTO
     * 
     * @param event Event entity
     * @return EventResponse DTO
     */
    private EventResponse convertToResponse(Event event) {
        return new EventResponse(
            event.getEventId(),
            event.getEventName(),
            event.getDescription(),
            event.getStartDateTime(),
            event.getEndDateTime(),
            event.getLocation(),
            event.getOrganizer(),
            event.getMaxParticipants(),
            event.getCurrentParticipants(),
            event.getStatus(),
            event.getCreatedAt(),
            event.getUpdatedAt()
        );
    }

    /**
     * Validate event request
     * 
     * @param request Event request
     * @throws IllegalArgumentException if validation fails
     */
    private void validateEventRequest(EventRequest request) {
        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new IllegalArgumentException("Start date/time must be before end date/time");
        }
        
        if (request.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date/time must be in the future");
        }
    }

    /**
     * Event Statistics DTO
     */
    public static class EventStatistics {
        private final int totalEvents;
        private final int activeEvents;
        private final int completedEvents;
        private final int cancelledEvents;

        public EventStatistics(int totalEvents, int activeEvents, int completedEvents, int cancelledEvents) {
            this.totalEvents = totalEvents;
            this.activeEvents = activeEvents;
            this.completedEvents = completedEvents;
            this.cancelledEvents = cancelledEvents;
        }

        public int getTotalEvents() {
            return totalEvents;
        }

        public int getActiveEvents() {
            return activeEvents;
        }

        public int getCompletedEvents() {
            return completedEvents;
        }

        public int getCancelledEvents() {
            return cancelledEvents;
        }
    }
}