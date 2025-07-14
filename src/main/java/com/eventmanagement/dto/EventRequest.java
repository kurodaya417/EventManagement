package com.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Event Request DTO
 * 
 * Data Transfer Object for creating/updating events.
 */
public class EventRequest {
    
    @NotBlank(message = "Event name is required")
    private String eventName;
    
    private String description;
    
    @NotNull(message = "Start date and time is required")
    @Future(message = "Start date and time must be in the future")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "End date and time is required")
    @Future(message = "End date and time must be in the future")
    private LocalDateTime endDateTime;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Organizer is required")
    private String organizer;
    
    @Positive(message = "Maximum participants must be positive")
    private Integer maxParticipants;

    // Constructors
    public EventRequest() {}

    public EventRequest(String eventName, String description, LocalDateTime startDateTime, 
                       LocalDateTime endDateTime, String location, String organizer, 
                       Integer maxParticipants) {
        this.eventName = eventName;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.organizer = organizer;
        this.maxParticipants = maxParticipants;
    }

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}