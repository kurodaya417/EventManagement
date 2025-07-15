package com.eventmanagement.service;

import com.eventmanagement.dto.ParticipantRequest;
import com.eventmanagement.dto.ParticipantResponse;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.Participant;
import com.eventmanagement.mapper.ParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Participant Service
 * 
 * Service layer for participant management operations.
 */
@Service
@Transactional
public class ParticipantService {

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private EventService eventService;

    /**
     * Get all participants for a specific event
     * 
     * @param eventId Event ID
     * @return List of participants
     */
    public List<ParticipantResponse> getParticipantsByEventId(Long eventId) {
        // Check if event exists
        eventService.getEventEntityById(eventId);
        
        List<Participant> participants = participantMapper.findByEventId(eventId);
        return participants.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all events for a specific participant
     * 
     * @param participantEmail Participant email
     * @return List of participant registrations
     */
    public List<ParticipantResponse> getEventsByParticipant(String participantEmail) {
        List<Participant> participants = participantMapper.findByParticipantEmail(participantEmail);
        return participants.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Register a participant for an event
     * 
     * @param request Participant registration request
     * @return Registered participant response
     */
    public ParticipantResponse registerParticipant(ParticipantRequest request) {
        // Validate event exists and is active
        Event event = eventService.getEventEntityById(request.getEventId());
        if (!"ACTIVE".equals(event.getStatus())) {
            throw new RuntimeException("Cannot register for non-active event");
        }

        // Check for duplicate registration
        Participant existingParticipant = participantMapper.findByEventIdAndEmail(
                request.getEventId(), request.getParticipantEmail());
        if (existingParticipant != null) {
            throw new RuntimeException("Participant already registered for this event");
        }

        // Check if event is full
        int currentParticipants = participantMapper.countByEventId(request.getEventId());
        if (currentParticipants >= event.getMaxParticipants()) {
            throw new RuntimeException("Event is fully booked");
        }

        // Create and save participant
        Participant participant = new Participant();
        participant.setEventId(request.getEventId());
        participant.setParticipantName(request.getParticipantName());
        participant.setParticipantEmail(request.getParticipantEmail());
        participant.setParticipantPhone(request.getParticipantPhone());
        participant.setRegisteredAt(LocalDateTime.now());

        participantMapper.insert(participant);

        // Update event's current participant count
        updateEventParticipantCount(request.getEventId());

        return convertToResponse(participant);
    }

    /**
     * Cancel a participant registration
     * 
     * @param participationId Participation ID
     * @return true if cancelled successfully, false otherwise
     */
    public boolean cancelParticipation(Long participationId) {
        Participant participant = participantMapper.findById(participationId);
        if (participant == null) {
            throw new RuntimeException("Participation not found");
        }

        Long eventId = participant.getEventId();
        int rowsAffected = participantMapper.deleteById(participationId);
        
        if (rowsAffected > 0) {
            // Update event's current participant count
            updateEventParticipantCount(eventId);
            return true;
        }
        return false;
    }

    /**
     * Cancel participant registration by event ID and email
     * 
     * @param eventId Event ID
     * @param participantEmail Participant email
     * @return true if cancelled successfully, false otherwise
     */
    public boolean cancelParticipationByEventAndEmail(Long eventId, String participantEmail) {
        Participant participant = participantMapper.findByEventIdAndEmail(eventId, participantEmail);
        if (participant == null) {
            throw new RuntimeException("Participation not found");
        }

        return cancelParticipation(participant.getParticipationId());
    }

    /**
     * Update the current participant count for an event
     * 
     * @param eventId Event ID
     */
    private void updateEventParticipantCount(Long eventId) {
        int currentCount = participantMapper.countByEventId(eventId);
        eventService.updateParticipantCount(eventId, currentCount);
    }

    /**
     * Convert Participant entity to ParticipantResponse DTO
     * 
     * @param participant Participant entity
     * @return ParticipantResponse DTO
     */
    private ParticipantResponse convertToResponse(Participant participant) {
        return new ParticipantResponse(
                participant.getParticipationId(),
                participant.getEventId(),
                participant.getParticipantName(),
                participant.getParticipantEmail(),
                participant.getParticipantPhone(),
                participant.getRegisteredAt()
        );
    }
}