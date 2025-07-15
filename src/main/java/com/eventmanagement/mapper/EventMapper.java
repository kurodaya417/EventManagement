package com.eventmanagement.mapper;

import com.eventmanagement.dto.EventSearchRequest;
import com.eventmanagement.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Event Mapper Interface
 * 
 * MyBatis mapper interface for Event operations.
 */
@Mapper
public interface EventMapper {
    
    /**
     * Find all events
     * 
     * @return List of all events
     */
    List<Event> findAll();
    
    /**
     * Find event by ID
     * 
     * @param eventId Event ID
     * @return Event if found, null otherwise
     */
    Event findById(@Param("eventId") Long eventId);
    
    /**
     * Find events by status
     * 
     * @param status Event status
     * @return List of events with specified status
     */
    List<Event> findByStatus(@Param("status") String status);
    
    /**
     * Find events by organizer
     * 
     * @param organizer Organizer name
     * @return List of events by organizer
     */
    List<Event> findByOrganizer(@Param("organizer") String organizer);
    
    /**
     * Insert new event
     * 
     * @param event Event to insert
     * @return Number of rows affected
     */
    int insert(Event event);
    
    /**
     * Update existing event
     * 
     * @param event Event to update
     * @return Number of rows affected
     */
    int update(Event event);
    
    /**
     * Delete event by ID
     * 
     * @param eventId Event ID
     * @return Number of rows affected
     */
    int deleteById(@Param("eventId") Long eventId);
    
    /**
     * Count total events
     * 
     * @return Total number of events
     */
    int countAll();
    
    /**
     * Count events by status
     * 
     * @param status Event status
     * @return Number of events with specified status
     */
    int countByStatus(@Param("status") String status);
    
    /**
     * Search events with advanced criteria
     * 
     * @param searchRequest Search criteria
     * @return List of events matching the search criteria
     */
    List<Event> searchEvents(EventSearchRequest searchRequest);
    
    /**
     * Count events matching search criteria
     * 
     * @param searchRequest Search criteria
     * @return Total number of events matching the search criteria
     */
    int countSearchEvents(EventSearchRequest searchRequest);
}