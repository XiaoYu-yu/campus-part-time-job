-- Campus relay schema skeleton
-- Execute inside an existing cangqiong_takeaway database

CREATE TABLE IF NOT EXISTS campus_customer_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    real_name VARCHAR(50) NOT NULL,
    identity_type VARCHAR(20) NOT NULL,
    identity_no VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS campus_courier_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    real_name VARCHAR(50) NOT NULL,
    student_no VARCHAR(50) NOT NULL,
    college VARCHAR(100) NOT NULL,
    major VARCHAR(100) NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    dormitory_building VARCHAR(50) NOT NULL,
    dormitory_room VARCHAR(50) NOT NULL,
    id_card_last4 VARCHAR(4) NOT NULL,
    emergency_contact_name VARCHAR(50) NOT NULL,
    emergency_contact_phone VARCHAR(20) NOT NULL,
    verification_photo_url VARCHAR(255),
    schedule_attachment_url VARCHAR(255),
    review_status VARCHAR(20) NOT NULL,
    review_comment VARCHAR(255),
    reviewed_by_employee_id BIGINT,
    reviewed_at DATETIME,
    enabled TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (reviewed_by_employee_id) REFERENCES employee(id)
);

CREATE TABLE IF NOT EXISTS campus_pickup_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    gate_area VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    enabled TINYINT DEFAULT 1,
    sort INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS campus_relay_order (
    id VARCHAR(32) PRIMARY KEY,
    customer_user_id BIGINT NOT NULL,
    courier_profile_id BIGINT,
    pickup_point_id BIGINT NOT NULL,
    delivery_target_type VARCHAR(30) NOT NULL,
    delivery_building VARCHAR(100) NOT NULL,
    delivery_detail VARCHAR(255) NOT NULL,
    delivery_contact_name VARCHAR(50) NOT NULL,
    delivery_contact_phone VARCHAR(20) NOT NULL,
    food_description VARCHAR(255) NOT NULL,
    external_platform_name VARCHAR(50),
    external_order_ref VARCHAR(100),
    pickup_code VARCHAR(50),
    base_fee DECIMAL(10,2) NOT NULL,
    priority_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    tip_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    order_status VARCHAR(30) NOT NULL,
    priority_dormitory_building VARCHAR(50),
    paid_at DATETIME,
    priority_window_deadline DATETIME,
    accepted_at DATETIME,
    cancel_locked_until DATETIME,
    picked_up_at DATETIME,
    delivered_at DATETIME,
    auto_complete_at DATETIME,
    cancelled_at DATETIME,
    after_sale_applied_at DATETIME,
    pickup_proof_image_url VARCHAR(255),
    cancel_reason VARCHAR(255),
    customer_remark VARCHAR(255),
    courier_remark VARCHAR(255),
    after_sale_reason VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_user_id) REFERENCES user(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id),
    FOREIGN KEY (pickup_point_id) REFERENCES campus_pickup_point(id)
);

CREATE TABLE IF NOT EXISTS campus_location_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    relay_order_id VARCHAR(32) NOT NULL,
    courier_profile_id BIGINT NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    source VARCHAR(30) NOT NULL,
    note VARCHAR(255),
    reported_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (relay_order_id) REFERENCES campus_relay_order(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id)
);

CREATE TABLE IF NOT EXISTS campus_settlement_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    relay_order_id VARCHAR(32) NOT NULL UNIQUE,
    courier_profile_id BIGINT NOT NULL,
    gross_amount DECIMAL(10,2) NOT NULL,
    platform_commission DECIMAL(10,2) NOT NULL DEFAULT 0,
    pending_amount DECIMAL(10,2) NOT NULL,
    settlement_status VARCHAR(20) NOT NULL,
    settled_at DATETIME,
    remark VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (relay_order_id) REFERENCES campus_relay_order(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id)
);
