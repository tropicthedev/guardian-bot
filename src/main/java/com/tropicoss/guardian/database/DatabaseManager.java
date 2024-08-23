package com.tropicoss.guardian.database;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Objects;

public class DatabaseManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
    private static final String DEFAULT_FILEPATH = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("guardian.db").toString();
    private static String filePath = "";
    private static Connection connection = null;

    private DatabaseManager(String filepath) throws SQLException {
        filePath = filepath;
    }

    private DatabaseManager() throws SQLException {
        filePath = DEFAULT_FILEPATH;
    }

    public static Connection getConnection() throws SQLException {

        if (Objects.equals(filePath, "")) {
            filePath = DEFAULT_FILEPATH;
        }

        if (connection == null) {
            connection = DriverManager.getConnection(filePath);
        }

        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void createDatabases() throws SQLException {

        String sqlCreateMemberTable = """
                    CREATE TABLE IF NOT EXISTS `members` (
                        `member_id` INTEGER NOT NULL,
                        `discord_id` TEXT NOT NULL,
                        `isAdmin` REAL NOT NULL DEFAULT 'FALSE',
                        `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (member_id, discord_id)
                    );
                """;

        String sqlCreateApplicationTable = """
                        CREATE TABLE IF NOT EXISTS `applications` (
                        `application_id` INTEGER PRIMARY KEY,
                        `content` TEXT NOT NULL,
                        `message_id` TEXT NOT NULL UNIQUE,
                        `discord_id` TEXT NOT NULL,
                        `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP
                    );
                """;

        String sqlCreateInterviewTable = """
                    CREATE TABLE IF NOT EXISTS `interviews` (
                        `interview_id` INTEGER PRIMARY KEY,
                        `application_id` INTEGER NOT NULL UNIQUE,
                        `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY(`application_id`) REFERENCES `application`(`application_id`)
                    );
                """;

        String sqlCreateApplicationResponseTable = """
                    CREATE TABLE IF NOT EXISTS `application_responses` (
                        `application_response_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                        `admin_id` INTEGER NOT NULL,
                        `application_id` INTEGER NOT NULL UNIQUE,
                        `content` TEXT,
                        `status` TEXT NOT NULL,
                        `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY(`admin_id`) REFERENCES `member`(`member_id`),
                        FOREIGN KEY(`application_id`) REFERENCES `application`(`application_id`)
                    );
                """;

        String sqlCreateInterviewResponseTable = """
                    CREATE TABLE IF NOT EXISTS `interview_responses` (
                        `interview_response_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                        `admin_id` INTEGER NOT NULL,
                        `interview_id` INTEGER NOT NULL UNIQUE,
                        `content` TEXT,
                        `status` TEXT NOT NULL,
                        `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY(`admin_id`) REFERENCES `member`(`member_id`),
                        FOREIGN KEY(`interview_id`) REFERENCES `interview`(`interview_id`)
                    );
                """;

        String sqlCreateServerTable = """
                    CREATE TABLE IF NOT EXISTS `servers` (
                        `server_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                        `name` TEXT NOT NULL,
                        `token` TEXT NOT NULL,
                        `created_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `modified_at` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP
                    );
                """;

        String sqlCreateSessionTable = """
                CREATE TABLE IF NOT EXISTS `sessions` (
                `session_id` INTEGER PRIMARY KEY NOT NULL UNIQUE,
                `member_id` INTEGER NOT NULL,
                `server_id` INTEGER NOT NULL,
                `session_start` REAL NOT NULL DEFAULT CURRENT_TIMESTAMP,
                `session_end` REAL,
                FOREIGN KEY(`member_id`) REFERENCES `member`(`member_id`),
                FOREIGN KEY(`server_id`) REFERENCES `server`(`server_id`)
                );
                """;
        Statement statement = getConnection().createStatement();

        try {
            connection.setAutoCommit(false);

            statement.execute(sqlCreateMemberTable);
            statement.execute(sqlCreateApplicationTable);
            statement.execute(sqlCreateInterviewTable);
            statement.execute(sqlCreateApplicationResponseTable);
            statement.execute(sqlCreateInterviewResponseTable);
            statement.execute(sqlCreateServerTable);
            statement.execute(sqlCreateSessionTable);

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error("An error occurred while checking database tables: {}", e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }

    public static void closeStatement(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    public static void closeResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
    }
}
