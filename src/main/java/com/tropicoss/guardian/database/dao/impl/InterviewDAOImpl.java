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
    private static Connection connection = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

    public InterviewDAOImpl(Connection connection) {
        InterviewDAOImpl.connection = connection;
    }

    public InterviewDAOImpl() throws SQLException{
        connection =  DatabaseManager.getConnection();
    }


    @Override
    public void addInterview(Interview interview) {
        String sql = "INSERT INTO interviews (interview_id, application_id, created_at, modified_at) VALUES (?, ?, ?, ?)";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, interview.getInterviewId());
            statement.setLong(2, interview.getApplicationId());
            statement.setTimestamp(3, Timestamp.valueOf(interview.getCreatedAt()));
            statement.setTimestamp(4, Timestamp.valueOf(interview.getModifiedAt()));

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public Interview getInterviewById(int interviewId) {
        String sql = "SELECT * FROM interviews WHERE interview_id = ?";
        Interview interview = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setInt(1, interviewId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                interview = new Interview();
                interview.setInterviewId(rs.getInt("interview_id"));
                interview.setApplicationId(rs.getInt("application_id"));
                interview.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                interview.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());
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

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Interview interview = new Interview();
                interview.setInterviewId(rs.getInt("interview_id"));
                interview.setApplicationId(rs.getInt("application_id"));
                interview.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                interview.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());

                interviews.add(interview);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return interviews;
    }

    @Override
    public void updateInterview(Interview interview) {
        String sql = "UPDATE interviews SET application_id = ?, modified_at = ? WHERE interview_id = ?";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, interview.getApplicationId());
            statement.setTimestamp(2, Timestamp.valueOf(interview.getModifiedAt()));
            statement.setLong(3, interview.getInterviewId());

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteInterview(int interviewId) {
        String sql = "DELETE FROM interviews WHERE interview_id = ?";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setInt(1, interviewId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
