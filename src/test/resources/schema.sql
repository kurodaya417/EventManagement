-- Test Database Schema for H2 (H2 compatible syntax)
CREATE TABLE events (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    location VARCHAR(255) NOT NULL,
    organizer VARCHAR(255) NOT NULL,
    max_participants INT NOT NULL,
    current_participants INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO events (event_name, description, start_date_time, end_date_time, location, organizer, max_participants, current_participants, status, created_at, updated_at)
VALUES ('Test Event 1', 'Test Description 1', '2025-01-01 10:00:00', '2025-01-01 12:00:00', 'Test Location 1', 'Test Organizer 1', 50, 10, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO events (event_name, description, start_date_time, end_date_time, location, organizer, max_participants, current_participants, status, created_at, updated_at)
VALUES ('Test Event 2', 'Test Description 2', '2025-01-02 14:00:00', '2025-01-02 16:00:00', 'Test Location 2', 'Test Organizer 2', 30, 5, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO events (event_name, description, start_date_time, end_date_time, location, organizer, max_participants, current_participants, status, created_at, updated_at)
VALUES ('Test Event 3', 'Test Description 3', '2025-01-03 09:00:00', '2025-01-03 11:00:00', 'Test Location 3', 'Test Organizer 1', 20, 0, 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);