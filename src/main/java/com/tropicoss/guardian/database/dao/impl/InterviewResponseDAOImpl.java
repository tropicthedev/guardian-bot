package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.InterviewResponseDAO;
import com.tropicoss.guardian.database.model.InterviewResponse;
import com.tropicoss.guardian.database.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InterviewResponseDAOImpl implements InterviewResponseDAO {
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
    private static Connection connection = null;

    public InterviewResponseDAOImpl(Connection connection) {
        InterviewResponseDAOImpl.connection = connection;
    }

    public InterviewResponseDAOImpl() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    @Override
    public void addInterviewResponse(InterviewResponse interviewResponse) throws SQLException {
        String sql = "INSERT INTO interview_responses (admin_id, interview_id, content, status, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, interviewResponse.getAdminId());
            statement.setLong(2, interviewResponse.getInterviewId());
            statement.setString(3, interviewResponse.getContent());
            statement.setString(4, interviewResponse.getStatus().name());
            statement.setTimestamp(5, Timestamp.valueOf(interviewResponse.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(interviewResponse.getModifiedAt()));

            statement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeStatement(statement);
        }
    }

    @Override
    public InterviewResponse getInterviewResponseById(long interviewResponseId) throws SQLException {
        String sql = "SELECT * FROM interview_responses WHERE interview_response_id = ?";
        InterviewResponse interviewResponse = null;

        PreparedStatement statement = null;

        try {

            statement = connection.prepareStatement(sql);

            statement.setLong(1, interviewResponseId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                interviewResponse = new InterviewResponse();
                interviewResponse.setInterviewResponseId(rs.getInt("interview_response_id"));
                interviewResponse.setAdminId(rs.getInt("admin_id"));
                interviewResponse.setInterviewId(rs.getInt("interview_id"));
                interviewResponse.setContent(rs.getString("content"));
                interviewResponse.setStatus(Status.valueOf(rs.getString("status")));
                interviewResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                interviewResponse.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeResultSet(statement.getResultSet());
        }

        return interviewResponse;
    }

    @Override
    public List<InterviewResponse> getAllInterviewResponses() throws SQLException {
        String sql = "SELECT * FROM interview_responses";
        List<InterviewResponse> interviewResponses = new ArrayList<>();

        Statement statement = null;

        try {

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                InterviewResponse interviewResponse = new InterviewResponse();
                interviewResponse.setInterviewResponseId(rs.getInt("interview_response_id"));
                interviewResponse.setAdminId(rs.getInt("admin_id"));
                interviewResponse.setInterviewId(rs.getInt("interview_id"));
                interviewResponse.setContent(rs.getString("content"));
                interviewResponse.setStatus(Status.valueOf(rs.getString("status")));
                interviewResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                interviewResponse.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());

                interviewResponses.add(interviewResponse);
            }
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeResultSet(statement.getResultSet());
        }

        return interviewResponses;
    }

    @Override
    public void updateInterviewResponse(InterviewResponse interviewResponse) throws SQLException {
        String sql = "UPDATE interview_responses SET admin_id = ?, interview_id = ?, content = ?, status = ?, modified_at = ? WHERE interview_response_id = ?";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, interviewResponse.getAdminId());
            statement.setLong(2, interviewResponse.getInterviewId());
            statement.setString(3, interviewResponse.getContent());
            statement.setString(4, interviewResponse.getStatus().name());
            statement.setTimestamp(5, Timestamp.valueOf(interviewResponse.getModifiedAt()));
            statement.setLong(6, interviewResponse.getInterviewResponseId());

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteInterviewResponse(long interviewResponseId) {
        String sql = "DELETE FROM interview_responses WHERE interview_response_id = ?";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, interviewResponseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public InterviewResponse getInterviewResponseByChannelId(long channelId) throws SQLException {
        String sql = "SELECT * FROM interview_responses WHERE interview_id = ?";
        InterviewResponse interviewResponse = null;

        PreparedStatement statement = null;

        try {

            statement = connection.prepareStatement(sql);

            statement.setLong(1, channelId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                interviewResponse = new InterviewResponse();
                interviewResponse.setInterviewResponseId(rs.getInt("interview_response_id"));
                interviewResponse.setAdminId(rs.getInt("admin_id"));
                interviewResponse.setInterviewId(rs.getInt("interview_id"));
                interviewResponse.setContent(rs.getString("content"));
                interviewResponse.setStatus(Status.valueOf(rs.getString("status")));
                interviewResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                interviewResponse.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeResultSet(statement.getResultSet());
        }

        return interviewResponse;
    }
}
