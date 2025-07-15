package com.eventmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Participant Registration Request DTO
 * 
 * Data Transfer Object for participant registration.
 */
public class ParticipantRequest {
    
    @NotNull(message = "Event ID is required")
    private Long eventId;
    
    @NotBlank(message = "Participant name is required")
    private String participantName;
    
    @Email(message = "Valid email address is required")
    @NotBlank(message = "Email is required")
    private String participantEmail;
    
    @Pattern(regexp = "^[0-9-+()\\s]*$", message = "Invalid phone number format")
    private String participantPhone;

    // Constructors
    public ParticipantRequest() {}

    public ParticipantRequest(Long eventId, String participantName, String participantEmail, String participantPhone) {
        this.eventId = eventId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.participantPhone = participantPhone;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public String getParticipantPhone() {
        return participantPhone;
    }

    public void setParticipantPhone(String participantPhone) {
        this.participantPhone = participantPhone;
    }
}