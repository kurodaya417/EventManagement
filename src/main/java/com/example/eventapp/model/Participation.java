package com.example.eventapp.model;

import java.time.LocalDateTime;

/**
 * 参加エンティティ
 */
public class Participation {
    private Long id;
    private Long eventId;
    private String participantName;
    private String participantEmail;
    private String participantPhone;
    private LocalDateTime registeredAt;

    // Default constructor
    public Participation() {}

    // Constructor
    public Participation(Long eventId, String participantName, String participantEmail, String participantPhone) {
        this.eventId = eventId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.participantPhone = participantPhone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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