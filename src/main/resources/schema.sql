-- Event Management Database Schema (Oracle)
-- This file contains the DDL statements for creating the database schema

-- Create sequence for event_id
CREATE SEQUENCE event_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create events table
CREATE TABLE events (
    event_id NUMBER(19) PRIMARY KEY,
    event_name VARCHAR2(255) NOT NULL,
    description CLOB,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    location VARCHAR2(255) NOT NULL,
    organizer VARCHAR2(255) NOT NULL,
    max_participants NUMBER(10) NOT NULL,
    current_participants NUMBER(10) DEFAULT 0,
    status VARCHAR2(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger for auto-increment event_id
CREATE OR REPLACE TRIGGER event_id_trigger
    BEFORE INSERT ON events
    FOR EACH ROW
BEGIN
    IF :NEW.event_id IS NULL THEN
        :NEW.event_id := event_id_seq.NEXTVAL;
    END IF;
END;
/

-- Create trigger for auto-update updated_at
CREATE OR REPLACE TRIGGER events_updated_at_trigger
    BEFORE UPDATE ON events
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- Create indexes for better performance
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_organizer ON events(organizer);
CREATE INDEX idx_events_start_date ON events(start_date_time);
CREATE INDEX idx_events_end_date ON events(end_date_time);

-- Insert sample data
INSERT INTO events (event_name, description, start_date_time, end_date_time, location, organizer, max_participants, current_participants, status)
VALUES 
    ('Spring Boot Workshop', 'Learn Spring Boot fundamentals and advanced features', 
     TIMESTAMP '2024-02-01 10:00:00', TIMESTAMP '2024-02-01 17:00:00', 
     'Conference Room A', 'John Doe', 30, 15, 'ACTIVE'),
    
    ('Database Design Seminar', 'Database design principles and best practices', 
     TIMESTAMP '2024-02-15 14:00:00', TIMESTAMP '2024-02-15 18:00:00', 
     'Online', 'Jane Smith', 50, 25, 'ACTIVE'),
    
    ('Tech Conference 2024', 'Annual technology conference with industry leaders', 
     TIMESTAMP '2024-03-01 09:00:00', TIMESTAMP '2024-03-03 18:00:00', 
     'Convention Center', 'Tech Committee', 500, 200, 'ACTIVE'),
    
    ('Team Building Event', 'Annual team building and networking event', 
     TIMESTAMP '2024-01-15 10:00:00', TIMESTAMP '2024-01-15 16:00:00', 
     'City Park', 'HR Department', 100, 85, 'COMPLETED');

-- Create sequence for participation_id
CREATE SEQUENCE participation_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create participants table
CREATE TABLE participants (
    participation_id NUMBER(19) PRIMARY KEY,
    event_id NUMBER(19) NOT NULL,
    participant_name VARCHAR2(100) NOT NULL,
    participant_email VARCHAR2(255) NOT NULL,
    participant_phone VARCHAR2(20),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_participants_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT uk_participants_event_email UNIQUE (event_id, participant_email)
);

-- Create trigger for auto-increment participation_id
CREATE OR REPLACE TRIGGER participation_id_trigger
    BEFORE INSERT ON participants
    FOR EACH ROW
BEGIN
    IF :NEW.participation_id IS NULL THEN
        :NEW.participation_id := participation_id_seq.NEXTVAL;
    END IF;
END;
/

-- Create indexes for better performance
CREATE INDEX idx_participants_event_id ON participants(event_id);
CREATE INDEX idx_participants_email ON participants(participant_email);
CREATE INDEX idx_participants_registered_at ON participants(registered_at);

-- Create sequence for user_id
CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create users table for authentication
CREATE TABLE users (
    user_id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    password VARCHAR2(100) NOT NULL,
    email VARCHAR2(255) UNIQUE NOT NULL,
    full_name VARCHAR2(100) NOT NULL,
    role VARCHAR2(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    enabled NUMBER(1) DEFAULT 1 CHECK (enabled IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger for auto-increment user_id
CREATE OR REPLACE TRIGGER user_id_trigger
    BEFORE INSERT ON users
    FOR EACH ROW
BEGIN
    IF :NEW.user_id IS NULL THEN
        :NEW.user_id := user_id_seq.NEXTVAL;
    END IF;
END;
/

-- Create trigger for auto-update updated_at
CREATE OR REPLACE TRIGGER users_updated_at_trigger
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, full_name, role, enabled)
VALUES ('admin', '$2a$10$UqPf7ixhdZHEez817SCpWeKDqIEZ.Yiz8s0J2pmu9xDj9l0NzUO2q', 'admin@eventmanagement.com', 'System Administrator', 'ADMIN', 1);

-- Insert default user (password: user123)
INSERT INTO users (username, password, email, full_name, role, enabled)
VALUES ('user', '$2a$10$rNMiiElMhYWXxk0tv3n4KON/qrBHAlx5j/yV1gC1g4JXki50c24Fq', 'user@eventmanagement.com', 'Regular User', 'USER', 1);

-- Commit the changes
COMMIT;