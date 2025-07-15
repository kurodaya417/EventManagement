package com.eventmanagement.dto;

import java.time.LocalDateTime;

/**
 * Participant Response DTO
 * 
 * Data Transfer Object for returning participant information.
 */
public class ParticipantResponse {
    private Long participationId;
    private Long eventId;
    private String participantName;
    private String participantEmail;
    private String participantPhone;
    private LocalDateTime registeredAt;

    // Constructors
    public ParticipantResponse() {}

    public ParticipantResponse(Long participationId, Long eventId, String participantName, 
                             String participantEmail, String participantPhone, 
                             LocalDateTime registeredAt) {
        this.participationId = participationId;
        this.eventId = eventId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.participantPhone = participantPhone;
        this.registeredAt = registeredAt;
    }

    // Getters and Setters
    public Long getParticipationId() {
        return participationId;
    }

    public void setParticipationId(Long participationId) {
        this.participationId = participationId;
    }

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

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}