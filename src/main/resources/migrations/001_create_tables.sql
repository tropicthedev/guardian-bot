-- Members table
CREATE TABLE IF NOT EXISTS `members` (
    `discord_id` TEXT PRIMARY KEY,
    `mojang_id` TEXT NOT NULL,
    `is_admin` INTEGER NOT NULL DEFAULT 0,
    `created_at` TEXT NOT NULL DEFAULT (datetime('now')),
    `modified_at` TEXT NOT NULL DEFAULT (datetime('now'))
);

-- Applications table
CREATE TABLE IF NOT EXISTS `applications` (
    `application_id` TEXT PRIMARY KEY,
    `content` TEXT NOT NULL,
    `discord_id` TEXT NOT NULL,
    `created_at` TEXT NOT NULL DEFAULT (datetime('now')),
    `modified_at` TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY(`discord_id`) REFERENCES `members`(`discord_id`)
);
CREATE INDEX idx_applications_discord_id ON applications(discord_id);

-- Interviews table
CREATE TABLE IF NOT EXISTS `interviews` (
    `interview_id` TEXT PRIMARY KEY,
    `application_id` TEXT NOT NULL UNIQUE,
    `created_at` TEXT NOT NULL DEFAULT (datetime('now')),
    `modified_at` TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY(`application_id`) REFERENCES `applications`(`application_id`)
);

-- Application responses table
CREATE TABLE IF NOT EXISTS `application_responses` (
    `application_response_id` TEXT PRIMARY KEY,
    `admin_id` TEXT NOT NULL,
    `application_id` TEXT NOT NULL UNIQUE,
    `content` TEXT,
    `status` TEXT NOT NULL,
    `created_at` TEXT NOT NULL DEFAULT (datetime('now')),
    `modified_at` TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY(`admin_id`) REFERENCES `members`(`discord_id`),
    FOREIGN KEY(`application_id`) REFERENCES `applications`(`application_id`)
);
CREATE INDEX idx_application_responses_admin_id ON application_responses(admin_id);

-- Interview responses table
CREATE TABLE IF NOT EXISTS `interview_responses` (
    `interview_response_id` TEXT PRIMARY KEY,
    `admin_id` TEXT NOT NULL,
    `interview_id` TEXT NOT NULL UNIQUE,
    `content` TEXT,
    `status` TEXT NOT NULL,
    `created_at` TEXT NOT NULL DEFAULT (datetime('now')),
    `modified_at` TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY(`admin_id`) REFERENCES `members`(`discord_id`),
    FOREIGN KEY(`interview_id`) REFERENCES `interviews`(`interview_id`)
);
CREATE INDEX idx_interview_responses_admin_id ON interview_responses(admin_id);

-- Servers table
CREATE TABLE IF NOT EXISTS `servers` (
    `server_id` TEXT PRIMARY KEY,
    `name` TEXT NOT NULL,
    `token` TEXT NOT NULL,
    `created_at` TEXT NOT NULL DEFAULT (datetime('now')),
    `modified_at` TEXT NOT NULL DEFAULT (datetime('now'))
);

-- Sessions table
CREATE TABLE IF NOT EXISTS `sessions` (
    `session_id` TEXT PRIMARY KEY,
    `discord_id` TEXT NOT NULL,
    `server_id` TEXT NOT NULL,
    `session_start` TEXT NOT NULL DEFAULT (datetime('now')),
    `session_end` TEXT,
    FOREIGN KEY(`discord_id`) REFERENCES `members`(`discord_id`),
    FOREIGN KEY(`server_id`) REFERENCES `servers`(`server_id`)
);
CREATE INDEX idx_sessions_discord_id ON sessions(discord_id);
CREATE INDEX idx_sessions_server_id ON sessions(server_id);