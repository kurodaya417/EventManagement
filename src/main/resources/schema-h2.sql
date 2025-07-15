-- H2 compatible schema for testing
-- Create users table for authentication
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create events table
CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    description CLOB,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    location VARCHAR(255) NOT NULL,
    organizer VARCHAR(255) NOT NULL,
    max_participants INT NOT NULL,
    current_participants INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create participants table
CREATE TABLE IF NOT EXISTS participants (
    participation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    participant_name VARCHAR(100) NOT NULL,
    participant_email VARCHAR(255) NOT NULL,
    participant_phone VARCHAR(20),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    UNIQUE (event_id, participant_email)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, full_name, role, enabled) VALUES 
('admin', '$2a$10$UqPf7ixhdZHEez817SCpWeKDqIEZ.Yiz8s0J2pmu9xDj9l0NzUO2q', 'admin@eventmanagement.com', 'System Administrator', 'ADMIN', TRUE);

-- Insert default user (password: user123)
INSERT INTO users (username, password, email, full_name, role, enabled) VALUES 
('user', '$2a$10$rNMiiElMhYWXxk0tv3n4KON/qrBHAlx5j/yV1gC1g4JXki50c24Fq', 'user@eventmanagement.com', 'Regular User', 'USER', TRUE);

-- Insert sample data for events
INSERT INTO events (event_name, description, start_date_time, end_date_time, location, organizer, max_participants, current_participants, status) VALUES 
('Spring Boot Workshop', 'Learn Spring Boot fundamentals and advanced features', 
 '2024-02-01 10:00:00', '2024-02-01 17:00:00', 
 'Conference Room A', 'John Doe', 30, 15, 'ACTIVE'),
('Database Design Seminar', 'Database design principles and best practices', 
 '2024-02-15 14:00:00', '2024-02-15 18:00:00', 
 'Online', 'Jane Smith', 50, 25, 'ACTIVE'),
('Tech Conference 2024', 'Annual technology conference with industry leaders', 
 '2024-03-01 09:00:00', '2024-03-03 18:00:00', 
 'Convention Center', 'Tech Committee', 500, 200, 'ACTIVE'),
('Team Building Event', 'Annual team building and networking event', 
 '2024-01-15 10:00:00', '2024-01-15 16:00:00', 
 'City Park', 'HR Department', 100, 85, 'COMPLETED');