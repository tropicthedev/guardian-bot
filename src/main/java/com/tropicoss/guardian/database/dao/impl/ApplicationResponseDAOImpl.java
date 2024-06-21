package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.ApplicationResponseDAO;
import com.tropicoss.guardian.database.model.ApplicationResponse;
import com.tropicoss.guardian.database.model.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicationResponseDAOImpl implements ApplicationResponseDAO {
    private final DatabaseManager databaseManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");


    public ApplicationResponseDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addApplicationResponse(ApplicationResponse applicationResponse) {
        String sql = "INSERT INTO application_responses (applicationResponseId, adminId, applicationId, content, status, createdAt, modifiedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationResponse.getApplicationResponseId());
            stmt.setInt(2, applicationResponse.getAdminId());
            stmt.setInt(3, applicationResponse.getApplicationId());
            stmt.setString(4, applicationResponse.getContent());
            stmt.setString(5, applicationResponse.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(applicationResponse.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(applicationResponse.getModifiedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public ApplicationResponse getApplicationResponseById(int applicationResponseId) {
        String sql = "SELECT * FROM application_responses WHERE applicationResponseId = ?";
        ApplicationResponse applicationResponse = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationResponseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationResponseId(rs.getInt("applicationResponseId"));
                applicationResponse.setAdminId(rs.getInt("adminId"));
                applicationResponse.setApplicationId(rs.getInt("applicationId"));
                applicationResponse.setContent(rs.getString("content"));
                applicationResponse.setStatus(Status.valueOf(rs.getString("status")));
                applicationResponse.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                applicationResponse.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return applicationResponse;
    }

    @Override
    public List<ApplicationResponse> getAllApplicationResponses() {
        String sql = "SELECT * FROM application_responses";
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ApplicationResponse applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationResponseId(rs.getInt("applicationResponseId"));
                applicationResponse.setAdminId(rs.getInt("adminId"));
                applicationResponse.setApplicationId(rs.getInt("applicationId"));
                applicationResponse.setContent(rs.getString("content"));
                applicationResponse.setStatus(Status.valueOf(rs.getString("status")));
                applicationResponse.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                applicationResponse.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                applicationResponses.add(applicationResponse);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return applicationResponses;
    }

    @Override
    public void updateApplicationResponse(ApplicationResponse applicationResponse) {
        String sql = "UPDATE application_responses SET adminId = ?, applicationId = ?, content = ?, status = ?, modifiedAt = ? WHERE applicationResponseId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationResponse.getAdminId());
            stmt.setInt(2, applicationResponse.getApplicationId());
            stmt.setString(3, applicationResponse.getContent());
            stmt.setString(4, applicationResponse.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(applicationResponse.getModifiedAt()));
            stmt.setInt(6, applicationResponse.getApplicationResponseId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteApplicationResponse(int applicationResponseId) {
        String sql = "DELETE FROM application_responses WHERE applicationResponseId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationResponseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
