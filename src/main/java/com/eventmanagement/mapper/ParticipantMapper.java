package com.eventmanagement.mapper;

import com.eventmanagement.entity.Participant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Participant Mapper Interface
 * 
 * MyBatis mapper interface for Participant operations.
 */
@Mapper
public interface ParticipantMapper {
    
    /**
     * Find all participants for a specific event
     * 
     * @param eventId Event ID
     * @return List of participants for the event
     */
    List<Participant> findByEventId(@Param("eventId") Long eventId);
    
    /**
     * Find all events for a specific participant
     * 
     * @param participantEmail Participant email
     * @return List of participants for the given email
     */
    List<Participant> findByParticipantEmail(@Param("participantEmail") String participantEmail);
    
    /**
     * Find participant by event ID and email (for duplicate check)
     * 
     * @param eventId Event ID
     * @param participantEmail Participant email
     * @return Participant if found, null otherwise
     */
    Participant findByEventIdAndEmail(@Param("eventId") Long eventId, @Param("participantEmail") String participantEmail);
    
    /**
     * Find participant by participation ID
     * 
     * @param participationId Participation ID
     * @return Participant if found, null otherwise
     */
    Participant findById(@Param("participationId") Long participationId);
    
    /**
     * Insert new participant
     * 
     * @param participant Participant to insert
     * @return Number of rows affected
     */
    int insert(Participant participant);
    
    /**
     * Delete participant by ID
     * 
     * @param participationId Participation ID
     * @return Number of rows affected
     */
    int deleteById(@Param("participationId") Long participationId);
    
    /**
     * Delete all participants for a specific event
     * 
     * @param eventId Event ID
     * @return Number of rows affected
     */
    int deleteByEventId(@Param("eventId") Long eventId);
    
    /**
     * Count participants for a specific event
     * 
     * @param eventId Event ID
     * @return Number of participants
     */
    int countByEventId(@Param("eventId") Long eventId);
}