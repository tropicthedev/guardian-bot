package com.tropicoss.guardian.database;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("guardian.sqlite").toString();
    private final Connection connection;

    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(URL);
        connection.setAutoCommit(false);
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void runMigrations(String migrationsDir) throws SQLException, IOException {
        MigrationManager migrationManager = new MigrationManager(connection, migrationsDir);
        migrationManager.runMigrations();
    }

    public void addUser(String discordId, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO members (discord_id, is_admin, created_at, modified_at) VALUES (?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, discordId);
            pstmt.setInt(2, isAdmin ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void updateUser(String discordId, boolean isAdmin) throws SQLException {
        String sql = "UPDATE members SET is_admin = ?, modified_at = datetime('now') WHERE discord_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, isAdmin ? 1 : 0);
            pstmt.setString(2, discordId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}

class MigrationManager {
    private final Connection connection;
    private final String migrationsDir;

    public MigrationManager(Connection connection, String migrationsDir) {
        this.connection = connection;
        this.migrationsDir = migrationsDir;
    }

    public void runMigrations() throws SQLException, IOException {
        createMigrationsTableIfNotExists();
        List<String> appliedMigrations = getAppliedMigrations();
        List<Path> migrationFiles = getMigrationFiles();

        for (Path migrationFile : migrationFiles) {
            String migrationName = migrationFile.getFileName().toString();
            if (!appliedMigrations.contains(migrationName)) {
                executeMigration(migrationFile);
                recordMigration(migrationName);
                System.out.println("Applied migration: " + migrationName);
            }
        }
    }

    private void createMigrationsTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS migrations ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL,"
                + "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
        connection.commit();
    }

    private List<String> getAppliedMigrations() throws SQLException {
        List<String> migrations = new ArrayList<>();
        String sql = "SELECT name FROM migrations ORDER BY applied_at";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                migrations.add(rs.getString("name"));
            }
        }
        connection.commit();
        return migrations;
    }

    private List<Path> getMigrationFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(migrationsDir))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sql"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private void executeMigration(Path migrationFile) throws IOException, SQLException {
        String sql = new String(Files.readAllBytes(migrationFile));
        String[] statements = sql.split(";");

        try (Statement stmt = connection.createStatement()) {
            for (String statement : statements) {
                // Trim to remove any leading/trailing whitespace
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
        connection.commit();
    }

    private void recordMigration(String migrationName) throws SQLException {
        String sql = "INSERT INTO migrations (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, migrationName);
            pstmt.executeUpdate();
        }
        connection.commit();
    }
}