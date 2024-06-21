package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.InterviewDAO;
import com.tropicoss.guardian.database.model.Interview;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InterviewDAOImpl implements InterviewDAO {
    private final DatabaseManager databaseManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");


    public InterviewDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addInterview(Interview interview) {
        String sql = "INSERT INTO interviews (interviewId, applicationId, createdAt, modifiedAt) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interview.getInterviewId());
            stmt.setInt(2, interview.getApplicationId());
            stmt.setTimestamp(3, Timestamp.valueOf(interview.getCreatedAt()));
            stmt.setTimestamp(4, Timestamp.valueOf(interview.getModifiedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public Interview getInterviewById(int interviewId) {
        String sql = "SELECT * FROM interviews WHERE interviewId = ?";
        Interview interview = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                interview = new Interview();
                interview.setInterviewId(rs.getInt("interviewId"));
                interview.setApplicationId(rs.getInt("applicationId"));
                interview.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                interview.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return interview;
    }

    @Override
    public List<Interview> getAllInterviews() {
        String sql = "SELECT * FROM interviews";
        List<Interview> interviews = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Interview interview = new Interview();
                interview.setInterviewId(rs.getInt("interviewId"));
                interview.setApplicationId(rs.getInt("applicationId"));
                interview.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                interview.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                interviews.add(interview);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return interviews;
    }

    @Override
    public void updateInterview(Interview interview) {
        String sql = "UPDATE interviews SET applicationId = ?, modifiedAt = ? WHERE interviewId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interview.getApplicationId());
            stmt.setTimestamp(2, Timestamp.valueOf(interview.getModifiedAt()));
            stmt.setInt(3, interview.getInterviewId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteInterview(int interviewId) {
        String sql = "DELETE FROM interviews WHERE interviewId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
