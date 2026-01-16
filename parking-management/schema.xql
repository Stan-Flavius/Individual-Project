
CREATE DATABASE IF NOT EXISTS parking_management;
USE parking_management;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'CUSTOMER', 'MANAGER'))
);

CREATE TABLE parking_lots (
    lot_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT chk_capacity CHECK (capacity > 0)
);

CREATE TABLE parking_spots (
    spot_number VARCHAR(10) PRIMARY KEY,
    lot_id VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE,
    current_reservation_id VARCHAR(20),
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE,
    CONSTRAINT chk_type CHECK (type IN ('REGULAR', 'VIP', 'DISABLED'))
);

CREATE TABLE reservations (
    reservation_id VARCHAR(20) PRIMARY KEY,
    spot_number VARCHAR(10) NOT NULL,
    vehicle_plate VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    daily_rate DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (spot_number) REFERENCES parking_spots(spot_number) ON DELETE CASCADE,
    CONSTRAINT chk_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_rate CHECK (daily_rate >= 0)
);

CREATE TABLE lot_spots (
    lot_id VARCHAR(20) NOT NULL,
    spot_number VARCHAR(10) NOT NULL,
    PRIMARY KEY (lot_id, spot_number),
    FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id) ON DELETE CASCADE
);

INSERT INTO users (username, password_hash, email, role) VALUES
('admin', '$2a$10$XPjXKfWWqKZ9g4JvVwGbwOy1jLXWGV8YQKhWxK9kFYKPUZd7y8pY6', 'admin@parking.com', 'ADMIN');

INSERT INTO parking_lots (lot_id, name, location, capacity) VALUES
('LOT001', 'Downtown Mall', '123 Main Street', 50),
('LOT002', 'Airport Parking', 'Airport Terminal 1', 200),
('LOT003', 'City Center', '456 Central Avenue', 100);

INSERT INTO parking_spots (spot_number, lot_id, type, is_occupied) VALUES
('A001', 'LOT001', 'REGULAR', FALSE),
('A002', 'LOT001', 'REGULAR', FALSE),
('A003', 'LOT001', 'DISABLED', FALSE),
('B001', 'LOT002', 'VIP', FALSE),
('B002', 'LOT002', 'REGULAR', FALSE),
('C001', 'LOT003', 'REGULAR', FALSE);

CREATE INDEX idx_spot_lot ON parking_spots(lot_id);
CREATE INDEX idx_spot_occupied ON parking_spots(is_occupied);
CREATE INDEX idx_reservation_customer ON reservations(customer_name);
CREATE INDEX idx_reservation_dates ON reservations(start_date, end_date);
CREATE INDEX idx_reservation_active ON reservations(is_active);

DESCRIBE users;
DESCRIBE parking_lots;
DESCRIBE parking_spots;
DESCRIBE reservations;