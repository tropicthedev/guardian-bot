package com.tropicoss.guardian.database;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class MigrationManager {
    private final Connection connection;
    private final String migrationsDir;

    public MigrationManager(Connection connection, String migrationsDir) {
        this.connection = connection;
        this.migrationsDir = migrationsDir;
    }

    public void runMigrations() throws SQLException, IOException {
        createMigrationsTableIfNotExists();
        List<String> appliedMigrations = getAppliedMigrations();
        List<Path> migrationFiles = getMigrationFilePaths();

        for (Path migrationFile : migrationFiles) {
            String migrationFileName = migrationFile.getFileName().toString();
            if (!appliedMigrations.contains(migrationFileName)) {
                try (BufferedReader reader = Files.newBufferedReader(migrationFile)) {
                    executeMigration(reader);
                    recordMigration(migrationFileName);
                    LOGGER.info("Applied migration: {}", migrationFileName);
                }
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

    private List<Path> getMigrationFilePaths() throws IOException {
        List<Path> migrationFiles = new ArrayList<>();
        ModContainer modContainer = FabricLoader.getInstance().getModContainer("guardian").orElseThrow();
        Path resourceDir = modContainer.findPath(migrationsDir).orElseThrow();

        try (var stream = Files.list(resourceDir)) {
            stream.filter(p -> p.toString().endsWith(".sql"))
                    .forEach(migrationFiles::add);
        }
        return migrationFiles;
    }

    private void executeMigration(BufferedReader reader) throws IOException, SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sqlBuilder.append(line).append("\n");
        }
        String sql = sqlBuilder.toString();
        String[] statements = sql.split(";");

        try (Statement stmt = connection.createStatement()) {
            for (String statement : statements) {
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
