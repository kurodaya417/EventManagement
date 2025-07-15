package com.eventmanagement.dto;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * Event Search Request DTO
 * 
 * Data transfer object for event search criteria.
 */
public class EventSearchRequest {
    
    private String keyword;          // Search in event name and description
    private String status;           // Event status (ACTIVE, COMPLETED, CANCELLED)
    private String organizer;        // Organizer name
    private String location;         // Location search
    private LocalDateTime startDateFrom;  // Start date range - from
    private LocalDateTime startDateTo;    // Start date range - to
    private LocalDateTime endDateFrom;    // End date range - from
    private LocalDateTime endDateTo;      // End date range - to
    
    // Pagination and sorting
    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size = 10;
    
    private String sortBy = "createdAt";        // Sort field
    private String sortOrder = "desc";          // Sort order (asc/desc)
    
    // Constructors
    public EventSearchRequest() {}
    
    public EventSearchRequest(String keyword, String status, String organizer, String location) {
        this.keyword = keyword;
        this.status = status;
        this.organizer = organizer;
        this.location = location;
    }
    
    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getOrganizer() {
        return organizer;
    }
    
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getStartDateFrom() {
        return startDateFrom;
    }
    
    public void setStartDateFrom(LocalDateTime startDateFrom) {
        this.startDateFrom = startDateFrom;
    }
    
    public LocalDateTime getStartDateTo() {
        return startDateTo;
    }
    
    public void setStartDateTo(LocalDateTime startDateTo) {
        this.startDateTo = startDateTo;
    }
    
    public LocalDateTime getEndDateFrom() {
        return endDateFrom;
    }
    
    public void setEndDateFrom(LocalDateTime endDateFrom) {
        this.endDateFrom = endDateFrom;
    }
    
    public LocalDateTime getEndDateTo() {
        return endDateTo;
    }
    
    public void setEndDateTo(LocalDateTime endDateTo) {
        this.endDateTo = endDateTo;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    // Helper methods
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
    
    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }
    
    public boolean hasOrganizer() {
        return organizer != null && !organizer.trim().isEmpty();
    }
    
    public boolean hasLocation() {
        return location != null && !location.trim().isEmpty();
    }
    
    public boolean hasStartDateRange() {
        return startDateFrom != null || startDateTo != null;
    }
    
    public boolean hasEndDateRange() {
        return endDateFrom != null || endDateTo != null;
    }
    
    public int getOffset() {
        return page * size;
    }
    
    @Override
    public String toString() {
        return "EventSearchRequest{" +
                "keyword='" + keyword + '\'' +
                ", status='" + status + '\'' +
                ", organizer='" + organizer + '\'' +
                ", location='" + location + '\'' +
                ", startDateFrom=" + startDateFrom +
                ", startDateTo=" + startDateTo +
                ", endDateFrom=" + endDateFrom +
                ", endDateTo=" + endDateTo +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}