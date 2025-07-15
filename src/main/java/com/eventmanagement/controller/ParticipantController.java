package com.eventmanagement.controller;

import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.dto.ParticipantRequest;
import com.eventmanagement.dto.ParticipantResponse;
import com.eventmanagement.service.ParticipantService;
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
 * Participant Controller
 * 
 * REST API controller for participant management operations.
 * Base URL: /api/participants
 */
@RestController
@RequestMapping("/participants")
@Tag(name = "Participant Management", description = "APIs for managing event participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    /**
     * Get all participants for a specific event
     * 
     * @param eventId Event ID
     * @return List of participants for the event
     */
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get participants by event ID", description = "Retrieve all participants for a specific event")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved participants"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getParticipantsByEventId(
            @Parameter(description = "Event ID", required = true) @PathVariable Long eventId) {
        try {
            List<ParticipantResponse> participants = participantService.getParticipantsByEventId(eventId);
            return ResponseEntity.ok(ApiResponse.success("Participants retrieved successfully", participants));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Event not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve participants: " + e.getMessage()));
        }
    }

    /**
     * Get all events for a specific participant
     * 
     * @param participantEmail Participant email
     * @return List of events for the participant
     */
    @GetMapping("/participant/{participantEmail}")
    @Operation(summary = "Get events by participant email", description = "Retrieve all events for a specific participant")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getEventsByParticipant(
            @Parameter(description = "Participant email", required = true) @PathVariable String participantEmail) {
        try {
            List<ParticipantResponse> events = participantService.getEventsByParticipant(participantEmail);
            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Register a participant for an event
     * 
     * @param request Participant registration request
     * @return Registered participant response
     */
    @PostMapping("/register")
    @Operation(summary = "Register participant for event", description = "Register a participant for a specific event")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Participant registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or business rule violation"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ParticipantResponse>> registerParticipant(
            @Valid @RequestBody ParticipantRequest request) {
        try {
            ParticipantResponse participant = participantService.registerParticipant(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Participant registered successfully", participant));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to register participant: " + e.getMessage()));
        }
    }

    /**
     * Cancel a participant registration
     * 
     * @param participationId Participation ID
     * @return Success response
     */
    @DeleteMapping("/cancel/{participationId}")
    @Operation(summary = "Cancel participant registration", description = "Cancel a participant registration by participation ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Participation cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Participation not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<String>> cancelParticipation(
            @Parameter(description = "Participation ID", required = true) @PathVariable Long participationId) {
        try {
            boolean cancelled = participantService.cancelParticipation(participationId);
            if (cancelled) {
                return ResponseEntity.ok(ApiResponse.success("Participation cancelled successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Participation not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Participation not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cancel participation: " + e.getMessage()));
        }
    }

    /**
     * Cancel participant registration by event and email
     * 
     * @param eventId Event ID
     * @param participantEmail Participant email
     * @return Success response
     */
    @DeleteMapping("/cancel/event/{eventId}/email/{participantEmail}")
    @Operation(summary = "Cancel participant registration by event and email", description = "Cancel a participant registration by event ID and email")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Participation cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Participation not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<String>> cancelParticipationByEventAndEmail(
            @Parameter(description = "Event ID", required = true) @PathVariable Long eventId,
            @Parameter(description = "Participant email", required = true) @PathVariable String participantEmail) {
        try {
            boolean cancelled = participantService.cancelParticipationByEventAndEmail(eventId, participantEmail);
            if (cancelled) {
                return ResponseEntity.ok(ApiResponse.success("Participation cancelled successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Participation not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Participation not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cancel participation: " + e.getMessage()));
        }
    }
}