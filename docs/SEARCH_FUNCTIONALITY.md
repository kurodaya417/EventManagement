# Search Functionality Extension Documentation

## Overview

This document describes the extended search functionality added to the EventManagement application.

## New Search Endpoint

### POST `/api/events/search`

Advanced search endpoint that allows filtering events by multiple criteria with pagination and sorting.

#### Request Body: `EventSearchRequest`

```json
{
  "keyword": "string",          // Search in event name and description
  "status": "string",           // Event status (ACTIVE, COMPLETED, CANCELLED)
  "organizer": "string",        // Organizer name (partial match)
  "location": "string",         // Location search (partial match)
  "startDateFrom": "datetime",  // Start date range - from
  "startDateTo": "datetime",    // Start date range - to
  "endDateFrom": "datetime",    // End date range - from
  "endDateTo": "datetime",      // End date range - to
  "page": 0,                    // Page number (default: 0)
  "size": 10,                   // Page size (default: 10, max: 100)
  "sortBy": "createdAt",        // Sort field (default: createdAt)
  "sortOrder": "desc"           // Sort order (asc/desc, default: desc)
}
```

#### Supported Sort Fields

- `eventName` - Event name
- `startDateTime` - Event start date/time
- `endDateTime` - Event end date/time
- `location` - Event location
- `organizer` - Event organizer
- `status` - Event status
- `createdAt` - Created timestamp (default)
- `updatedAt` - Updated timestamp

#### Response: `EventSearchResult`

```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": {
    "events": [
      {
        "eventId": 1,
        "eventName": "Spring Boot Workshop",
        "description": "Learn Spring Boot fundamentals",
        "startDateTime": "2024-02-01T10:00:00",
        "endDateTime": "2024-02-01T17:00:00",
        "location": "Conference Room A",
        "organizer": "John Doe",
        "maxParticipants": 30,
        "currentParticipants": 15,
        "status": "ACTIVE",
        "createdAt": "2024-01-01T09:00:00",
        "updatedAt": "2024-01-01T09:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

## Usage Examples

### Basic Keyword Search

```bash
curl -X POST http://localhost:8080/api/events/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Spring Boot"
  }'
```

### Advanced Search with Multiple Criteria

```bash
curl -X POST http://localhost:8080/api/events/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Workshop",
    "status": "ACTIVE",
    "organizer": "John",
    "location": "Conference Room",
    "startDateFrom": "2024-01-01T00:00:00",
    "startDateTo": "2024-12-31T23:59:59",
    "page": 0,
    "size": 5,
    "sortBy": "startDateTime",
    "sortOrder": "asc"
  }'
```

### Pagination Example

```bash
curl -X POST http://localhost:8080/api/events/search \
  -H "Content-Type: application/json" \
  -d '{
    "page": 1,
    "size": 20,
    "sortBy": "eventName",
    "sortOrder": "asc"
  }'
```

## Search Features

### 1. Text Search
- Searches in both event name and description
- Uses `LIKE` operator for partial matches
- Case-insensitive search

### 2. Status Filtering
- Exact match filter for event status
- Supports: ACTIVE, COMPLETED, CANCELLED

### 3. Organizer Search
- Partial match search in organizer name
- Case-insensitive search

### 4. Location Search
- Partial match search in location field
- Case-insensitive search

### 5. Date Range Filtering
- Supports filtering by start date range
- Supports filtering by end date range
- Can filter by both start and end date ranges independently

### 6. Pagination
- Configurable page size (max 100)
- Zero-based page numbering
- Includes pagination metadata in response

### 7. Sorting
- Multiple sort field options
- Ascending/descending order
- Defaults to created date descending

## Validation Rules

### Search Request Validation
- Page number must be non-negative
- Page size must be between 1 and 100
- Date ranges must be valid (from <= to)
- Sort field must be one of the supported fields
- Sort order must be 'asc' or 'desc'

### Error Responses

```json
{
  "success": false,
  "message": "Invalid search criteria: Page size must be at least 1",
  "data": null
}
```

## Database Performance

The search functionality leverages existing database indexes:
- `idx_events_status` - for status filtering
- `idx_events_organizer` - for organizer search
- `idx_events_start_date` - for start date filtering
- `idx_events_end_date` - for end date filtering

## Integration with Existing API

The new search endpoint complements the existing endpoints:
- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get specific event
- `GET /api/events/status/{status}` - Get events by status
- `GET /api/events/organizer/{organizer}` - Get events by organizer

## Testing

Comprehensive unit tests are provided in `EventControllerSearchTest.java` covering:
- Basic search functionality
- All filter combinations
- Pagination
- Sorting
- Validation
- Error handling

Run tests with:
```bash
mvn test -Dtest=EventControllerSearchTest
```