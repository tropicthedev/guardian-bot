package com.tropicoss.guardian.database;

import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

public class DatabaseManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
    private final String FILEPATH;
    private final Connection CONNECTION;

    public DatabaseManager(String filepath) throws SQLException {
        FILEPATH = "jdbc:sqlite:" + filepath;
        CONNECTION = DriverManager.getConnection(FILEPATH);
    }

    public Connection getConnection() throws SQLException {
        return CONNECTION;
    }

    public boolean createDatabases() throws SQLException {

        String sqlCreateMemberTable = """
            CREATE TABLE IF NOT EXISTS `member` (
                `member_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `discord_id` TEXT NOT NULL,
                `isAdmin` REAL NOT NULL DEFAULT 'FALSE',
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String sqlCreateMojangAccountTable = """
            CREATE TABLE IF NOT EXISTS `mojang_account` (
                `mojang_account_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `member_id` INTEGER NOT NULL,
                `mojang_id` TEXT NOT NULL UNIQUE,
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(`member_id`) REFERENCES `member`(`member_id`)
            );
        """;

        String sqlCreateApplicationTable = """
            CREATE TABLE IF NOT EXISTS `application` (
                `application_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `content` TEXT NOT NULL,
                `discord_id` TEXT NOT NULL,
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String sqlCreateInterviewTable = """
            CREATE TABLE IF NOT EXISTS `interview` (
                `interview_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `application_id` INTEGER NOT NULL,
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(`application_id`) REFERENCES `application`(`application_id`)
            );
        """;

        String sqlCreateApplicationResponseTable = """
            CREATE TABLE IF NOT EXISTS `application_response` (
                `application_response_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `admin_id` INTEGER NOT NULL,
                `application_id` INTEGER NOT NULL,
                `content` TEXT,
                `status` TEXT NOT NULL,
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(`admin_id`) REFERENCES `member`(`member_id`),
                FOREIGN KEY(`application_id`) REFERENCES `application`(`application_id`)
            );
        """;

        String sqlCreateInterviewResponseTable = """
            CREATE TABLE IF NOT EXISTS `interview_response` (
                `interview_response_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `admin_id` INTEGER NOT NULL,
                `interview_id` INTEGER NOT NULL,
                `content` TEXT,
                `status` TEXT NOT NULL,
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(`admin_id`) REFERENCES `member`(`member_id`),
                FOREIGN KEY(`interview_id`) REFERENCES `interview`(`interview_id`)
            );
        """;

        String sqlCreateServerTable = """
            CREATE TABLE IF NOT EXISTS `server` (
                `server_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `name` TEXT NOT NULL,
                `token` TEXT NOT NULL,
                `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String sqlCreateSessionTable = """
            CREATE TABLE IF NOT EXISTS `session` (
                `session_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `member_id` INTEGER NOT NULL,
                `server_id` INTEGER NOT NULL,
                `session_start` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `session_end` REAL,
                FOREIGN KEY(`member_id`) REFERENCES `member`(`member_id`),
                FOREIGN KEY(`server_id`) REFERENCES `server`(`server_id`)
            );
        """;

        try {
            CONNECTION.setAutoCommit(false);

            Statement stmt = CONNECTION.createStatement();

            stmt.execute(sqlCreateMemberTable);
            stmt.execute(sqlCreateMojangAccountTable);
            stmt.execute(sqlCreateApplicationTable);
            stmt.execute(sqlCreateInterviewTable);
            stmt.execute(sqlCreateApplicationResponseTable);
            stmt.execute(sqlCreateInterviewResponseTable);
            stmt.execute(sqlCreateServerTable);
            stmt.execute(sqlCreateSessionTable);
            CONNECTION.commit();

            return true;
        } catch (SQLException e) {

            CONNECTION.rollback();
            LOGGER.error("An error occurred while checking database tables: {}", e.getMessage());
            return false;
        }
    }
}
