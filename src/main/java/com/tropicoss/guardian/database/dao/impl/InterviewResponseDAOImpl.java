package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.InterviewResponseDAO;
import com.tropicoss.guardian.database.model.InterviewResponse;
import com.tropicoss.guardian.database.model.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InterviewResponseDAOImpl implements InterviewResponseDAO {
    private final DatabaseManager databaseManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

    public InterviewResponseDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addInterviewResponse(InterviewResponse interviewResponse) {
        String sql = "INSERT INTO interview_responses (interviewResponseId, adminId, interviewId, content, status, createdAt, modifiedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewResponse.getInterviewResponseId());
            stmt.setInt(2, interviewResponse.getAdminId());
            stmt.setInt(3, interviewResponse.getInterviewId());
            stmt.setString(4, interviewResponse.getContent());
            stmt.setString(5, interviewResponse.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(interviewResponse.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(interviewResponse.getModifiedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public InterviewResponse getInterviewResponseById(int interviewResponseId) {
        String sql = "SELECT * FROM interview_responses WHERE interviewResponseId = ?";
        InterviewResponse interviewResponse = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewResponseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                interviewResponse = new InterviewResponse();
                interviewResponse.setInterviewResponseId(rs.getInt("interviewResponseId"));
                interviewResponse.setAdminId(rs.getInt("adminId"));
                interviewResponse.setInterviewId(rs.getInt("interviewId"));
                interviewResponse.setContent(rs.getString("content"));
                interviewResponse.setStatus(Status.valueOf(rs.getString("status")));
                interviewResponse.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                interviewResponse.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return interviewResponse;
    }

    @Override
    public List<InterviewResponse> getAllInterviewResponses() {
        String sql = "SELECT * FROM interview_responses";
        List<InterviewResponse> interviewResponses = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InterviewResponse interviewResponse = new InterviewResponse();
                interviewResponse.setInterviewResponseId(rs.getInt("interviewResponseId"));
                interviewResponse.setAdminId(rs.getInt("adminId"));
                interviewResponse.setInterviewId(rs.getInt("interviewId"));
                interviewResponse.setContent(rs.getString("content"));
                interviewResponse.setStatus(Status.valueOf(rs.getString("status")));
                interviewResponse.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                interviewResponse.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                interviewResponses.add(interviewResponse);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return interviewResponses;
    }

    @Override
    public void updateInterviewResponse(InterviewResponse interviewResponse) {
        String sql = "UPDATE interview_responses SET adminId = ?, interviewId = ?, content = ?, status = ?, modifiedAt = ? WHERE interviewResponseId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewResponse.getAdminId());
            stmt.setInt(2, interviewResponse.getInterviewId());
            stmt.setString(3, interviewResponse.getContent());
            stmt.setString(4, interviewResponse.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(interviewResponse.getModifiedAt()));
            stmt.setInt(6, interviewResponse.getInterviewResponseId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteInterviewResponse(int interviewResponseId) {
        String sql = "DELETE FROM interview_responses WHERE interviewResponseId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewResponseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
