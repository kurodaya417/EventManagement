package com.eventmanagement.controller;

import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.dto.EventRequest;
import com.eventmanagement.dto.EventResponse;
import com.eventmanagement.dto.EventSearchRequest;
import com.eventmanagement.dto.EventSearchResult;
import com.eventmanagement.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Event Controller
 * 
 * REST API controller for event management operations.
 * Base URL: /api/events
 */
@RestController
@RequestMapping("/events")
@Tag(name = "Event Management", description = "APIs for managing events")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Get all events
     * 
     * @return List of all events
     */
    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieve all events in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents() {
        try {
            List<EventResponse> events = eventService.getAllEvents();
            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Get event by ID
     * 
     * @param id Event ID
     * @return Event details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved event"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(
            @Parameter(description = "Event ID", required = true) @PathVariable Long id) {
        try {
            EventResponse event = eventService.getEventById(id);
            if (event != null) {
                return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", event));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Event not found with ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve event: " + e.getMessage()));
        }
    }

    /**
     * Get events by status
     * 
     * @param status Event status
     * @return List of events with specified status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get events by status", description = "Retrieve events filtered by status")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByStatus(
            @Parameter(description = "Event status (ACTIVE, COMPLETED, CANCELLED)", required = true) 
            @PathVariable String status) {
        try {
            List<EventResponse> events = eventService.getEventsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Get events by organizer
     * 
     * @param organizer Organizer name
     * @return List of events by organizer
     */
    @GetMapping("/organizer/{organizer}")
    @Operation(summary = "Get events by organizer", description = "Retrieve events filtered by organizer")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByOrganizer(
            @Parameter(description = "Organizer name", required = true) @PathVariable String organizer) {
        try {
            List<EventResponse> events = eventService.getEventsByOrganizer(organizer);
            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Create new event
     * 
     * @param request Event request
     * @return Created event
     */
    @PostMapping
    @Operation(summary = "Create new event", description = "Create a new event")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Successfully created event"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Parameter(description = "Event request data", required = true) 
            @Valid @RequestBody EventRequest request) {
        try {
            EventResponse createdEvent = eventService.createEvent(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Event created successfully", createdEvent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create event: " + e.getMessage()));
        }
    }

    /**
     * Update existing event
     * 
     * @param id Event ID
     * @param request Event request
     * @return Updated event
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update event", description = "Update an existing event")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully updated event"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @Parameter(description = "Event ID", required = true) @PathVariable Long id,
            @Parameter(description = "Event request data", required = true) 
            @Valid @RequestBody EventRequest request) {
        try {
            EventResponse updatedEvent = eventService.updateEvent(id, request);
            if (updatedEvent != null) {
                return ResponseEntity.ok(ApiResponse.success("Event updated successfully", updatedEvent));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Event not found with ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update event: " + e.getMessage()));
        }
    }

    /**
     * Delete event
     * 
     * @param id Event ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event", description = "Delete an existing event")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully deleted event"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<String>> deleteEvent(
            @Parameter(description = "Event ID", required = true) @PathVariable Long id) {
        try {
            boolean deleted = eventService.deleteEvent(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", "Event ID: " + id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Event not found with ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete event: " + e.getMessage()));
        }
    }

    /**
     * Get event statistics
     * 
     * @return Event statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get event statistics", description = "Retrieve event statistics")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventService.EventStatistics>> getEventStatistics() {
        try {
            EventService.EventStatistics statistics = eventService.getEventStatistics();
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Search events with advanced criteria
     * 
     * @param searchRequest Search criteria
     * @return Paginated search results
     */
    @PostMapping("/search")
    @Operation(summary = "Search events", description = "Search events with advanced criteria including text search, date ranges, and pagination")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid search criteria"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventSearchResult>> searchEvents(
            @Parameter(description = "Search criteria", required = true)
            @Valid @RequestBody EventSearchRequest searchRequest) {
        try {
            EventSearchResult searchResult = eventService.searchEvents(searchRequest);
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", searchResult));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid search criteria: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search events: " + e.getMessage()));
        }
    }
}