package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.ApplicationResponseDAO;
import com.tropicoss.guardian.database.model.ApplicationResponse;
import com.tropicoss.guardian.database.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ApplicationResponseDAOImpl implements ApplicationResponseDAO {
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
    private static Connection connection = null;

    public ApplicationResponseDAOImpl(Connection connection) {
        ApplicationResponseDAOImpl.connection = connection;
    }

    public ApplicationResponseDAOImpl() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    @Override
    public ApplicationResponse getApplicationResponseById(int applicationResponseId) throws SQLException {
        String sql = "SELECT * FROM application_responses WHERE application_response_id = ?";
        ApplicationResponse applicationResponse = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setInt(1, applicationResponseId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationResponseId(resultSet.getInt("application_response_id"));
                applicationResponse.setAdminId(resultSet.getInt("admin_id"));
                applicationResponse.setApplicationId(resultSet.getInt("application_id"));
                applicationResponse.setContent(resultSet.getString("content"));
                applicationResponse.setStatus(Status.valueOf(resultSet.getString("status")));
                applicationResponse.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                applicationResponse.setModifiedAt(resultSet.getTimestamp("modified_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(resultSet);
            DatabaseManager.closeStatement(statement);
        }

        return applicationResponse;
    }

    @Override
    public List<ApplicationResponse> getAllApplicationResponses() {
        String sql = "SELECT * FROM application_responses";
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ApplicationResponse applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationResponseId(resultSet.getInt("application_response_id"));
                applicationResponse.setAdminId(resultSet.getInt("admin_id"));
                applicationResponse.setApplicationId(resultSet.getInt("application_id"));
                applicationResponse.setContent(resultSet.getString("content"));
                applicationResponse.setStatus(Status.valueOf(resultSet.getString("status")));
                applicationResponse.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                applicationResponse.setModifiedAt(resultSet.getTimestamp("modified_at").toLocalDateTime());

                applicationResponses.add(applicationResponse);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return applicationResponses;
    }

    @Override
    public void upsertApplicationResponse(ApplicationResponse applicationResponse) throws SQLException {
        String sql = "INSERT INTO application_responses (application_response_id, admin_id, application_id, content, status, modified_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(application_response_id) " +
                "DO UPDATE SET admin_id = excluded.admin_id, " +
                "application_id = excluded.application_id, " +
                "content = excluded.content, " +
                "status = excluded.status, " +
                "modified_at = excluded.modified_at";

        PreparedStatement statement = connection.prepareStatement(sql);

        try {
            statement.setLong(1, applicationResponse.getApplicationResponseId());
            statement.setLong(2, applicationResponse.getAdminId());
            statement.setLong(3, applicationResponse.getApplicationId());
            statement.setString(4, applicationResponse.getContent());
            statement.setString(5, applicationResponse.getStatus().name());
            statement.setTimestamp(6, Timestamp.valueOf(applicationResponse.getModifiedAt()));

            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            connection.rollback();  // Roll back in case of an error
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    @Override
    public void deleteApplicationResponse(int applicationResponseId) throws SQLException {
        String sql = "DELETE FROM application_responses WHERE applicationResponseId = ?";

        PreparedStatement statement = connection.prepareStatement(sql);

        try {
            statement.setInt(1, applicationResponseId);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public ApplicationResponse getApplicationResponseByApplicationId(long applicationId) throws SQLException {
        String sql = "SELECT * FROM application_responses WHERE application_id = ?";
        ApplicationResponse applicationResponse = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, applicationId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationResponseId(resultSet.getInt("application_response_id"));
                applicationResponse.setAdminId(resultSet.getInt("admin_id"));
                applicationResponse.setApplicationId(resultSet.getInt("application_id"));
                applicationResponse.setContent(resultSet.getString("content"));
                applicationResponse.setStatus(Status.valueOf(resultSet.getString("status")));
                applicationResponse.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                applicationResponse.setModifiedAt(resultSet.getTimestamp("modified_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(resultSet);
            DatabaseManager.closeStatement(statement);
        }

        return applicationResponse;
    }

    @Override
    public ApplicationResponse getApplicationResponseByMessageId(long messageId) throws SQLException {
        String sql = "SELECT ar.application_response_id, ar.admin_id, ar.application_id, ar.content, ar.status, ar.created_at, ar.modified_at\n" +
                "FROM application_responses ar INNER JOIN applications a on ar.application_id = a.application_id WHERE message_id = ?";
        ApplicationResponse applicationResponse = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, messageId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationResponseId(resultSet.getInt("application_response_id"));
                applicationResponse.setAdminId(resultSet.getInt("admin_id"));
                applicationResponse.setApplicationId(resultSet.getInt("application_id"));
                applicationResponse.setContent(resultSet.getString("content"));
                applicationResponse.setStatus(Status.valueOf(resultSet.getString("status")));
                applicationResponse.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                applicationResponse.setModifiedAt(resultSet.getTimestamp("modified_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(resultSet);
            DatabaseManager.closeStatement(statement);
        }

        return applicationResponse;
    }

    @Override
    public void resetApplication(long applicationId) throws SQLException {
        String sql = "UPDATE application_responses SET status = ?, modified_at = ? WHERE application_response_id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);

        try {
            statement.setString(1, Status.RESET.toString());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
