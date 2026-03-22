CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role_code VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    project_id BIGINT NULL,
    worker_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_app_user_username (username)
);

CREATE TABLE IF NOT EXISTS project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_team_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS worker (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS material_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    unit_code VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS point_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_item_id BIGINT NOT NULL,
    unit_code VARCHAR(20) NOT NULL,
    base_point DECIMAL(10, 2) NOT NULL,
    condition_code VARCHAR(20) NOT NULL,
    condition_factor DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_point_rule_material_item FOREIGN KEY (material_item_id) REFERENCES material_item (id)
);

CREATE TABLE IF NOT EXISTS point_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    balance INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_point_account_project_worker (project_id, worker_id),
    CONSTRAINT fk_point_account_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_point_account_worker FOREIGN KEY (worker_id) REFERENCES worker (id)
);

CREATE TABLE IF NOT EXISTS recycle_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    material_item_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_code VARCHAR(20) NOT NULL,
    condition_code VARCHAR(20) NOT NULL,
    calculated_points INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    remark VARCHAR(500) NULL,
    submitted_at DATETIME NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_recycle_record_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_recycle_record_team FOREIGN KEY (team_id) REFERENCES team (id),
    CONSTRAINT fk_recycle_record_worker FOREIGN KEY (worker_id) REFERENCES worker (id),
    CONSTRAINT fk_recycle_record_material_item FOREIGN KEY (material_item_id) REFERENCES material_item (id)
);

CREATE TABLE IF NOT EXISTS recycle_photo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recycle_record_id BIGINT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    storage_type VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    file_size BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recycle_photo_record FOREIGN KEY (recycle_record_id) REFERENCES recycle_record (id)
);

CREATE TABLE IF NOT EXISTS point_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    point_account_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    biz_type VARCHAR(30) NOT NULL,
    biz_id BIGINT NOT NULL,
    change_amount INT NOT NULL,
    balance_after INT NOT NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_point_ledger_account FOREIGN KEY (point_account_id) REFERENCES point_account (id),
    CONSTRAINT fk_point_ledger_worker FOREIGN KEY (worker_id) REFERENCES worker (id),
    CONSTRAINT fk_point_ledger_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS reward_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    points_cost INT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exchange_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL,
    project_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    reward_item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_points INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at DATETIME NULL,
    delivered_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_exchange_order_no (order_no),
    CONSTRAINT fk_exchange_order_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_exchange_order_worker FOREIGN KEY (worker_id) REFERENCES worker (id),
    CONSTRAINT fk_exchange_order_reward_item FOREIGN KEY (reward_item_id) REFERENCES reward_item (id)
);

CREATE TABLE IF NOT EXISTS file_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    storage_type VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
