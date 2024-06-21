package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.ApplicationDAO;
import com.tropicoss.guardian.database.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAOImpl implements ApplicationDAO {
    private final DatabaseManager databaseManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

    public ApplicationDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addApplication(Application application) {
        String sql = "INSERT INTO applications (applicationId, content, discordId, createdAt, modifiedAt) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, application.getApplicationId());
            stmt.setString(2, application.getContent());
            stmt.setString(3, application.getDiscordId());
            stmt.setTimestamp(4, Timestamp.valueOf(application.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(application.getModifiedAt()));

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public Application getApplicationById(int applicationId) {
        String sql = "SELECT * FROM applications WHERE applicationId = ?";
        Application application = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                application = new Application();
                application.setApplicationId(rs.getInt("applicationId"));
                application.setContent(rs.getString("content"));
                application.setDiscordId(rs.getString("discordId"));
                application.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                application.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return application;
    }

    @Override
    public List<Application> getAllApplications() {
        String sql = "SELECT * FROM applications";
        List<Application> applications = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Application application = new Application();
                application.setApplicationId(rs.getInt("applicationId"));
                application.setContent(rs.getString("content"));
                application.setDiscordId(rs.getString("discordId"));
                application.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                application.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                applications.add(application);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return applications;
    }

    @Override
    public void updateApplication(Application application) {
        String sql = "UPDATE applications SET content = ?, discordId = ?, modifiedAt = ? WHERE applicationId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, application.getContent());
            stmt.setString(2, application.getDiscordId());
            stmt.setTimestamp(3, Timestamp.valueOf(application.getModifiedAt()));
            stmt.setInt(4, application.getApplicationId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteApplication(int applicationId) {
        String sql = "DELETE FROM applications WHERE applicationId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
