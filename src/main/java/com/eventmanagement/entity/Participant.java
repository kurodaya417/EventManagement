package com.eventmanagement.entity;

import java.time.LocalDateTime;

/**
 * Participant Entity
 * 
 * Represents a participant registration for an event.
 */
public class Participant {
    private Long participationId;
    private Long eventId;
    private String participantName;
    private String participantEmail;
    private String participantPhone;
    private LocalDateTime registeredAt;

    // Constructors
    public Participant() {}

    public Participant(Long eventId, String participantName, String participantEmail, String participantPhone) {
        this.eventId = eventId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.participantPhone = participantPhone;
        this.registeredAt = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Participant{" +
                "participationId=" + participationId +
                ", eventId=" + eventId +
                ", participantName='" + participantName + '\'' +
                ", participantEmail='" + participantEmail + '\'' +
                ", participantPhone='" + participantPhone + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}