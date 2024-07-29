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
    private static Connection connection = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

    public ApplicationResponseDAOImpl(Connection connection) {
        ApplicationResponseDAOImpl.connection = connection;
    }

    public ApplicationResponseDAOImpl() throws SQLException{
        connection =  DatabaseManager.getConnection();
    }

    @Override
    public void addApplicationResponse(ApplicationResponse applicationResponse) throws SQLException {
        String sql = "INSERT INTO application_responses (admin_id, application_id, content, status, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = null;

        try{
            statement = connection.prepareStatement(sql);
            statement.setLong(1, applicationResponse.getAdminId());
            statement.setLong(2, applicationResponse.getApplicationId());
            statement.setString(3, applicationResponse.getContent());
            statement.setString(4, applicationResponse.getStatus().name());
            statement.setTimestamp(5, Timestamp.valueOf(applicationResponse.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(applicationResponse.getModifiedAt()));

            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        }

        finally {
            DatabaseManager.closeStatement(statement);
        }
    }

    @Override
    public ApplicationResponse getApplicationResponseById(int applicationResponseId) throws SQLException {
        String sql = "SELECT * FROM application_responses WHERE application_response_id = ?";
        ApplicationResponse applicationResponse = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try  {
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
        }

        finally {
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
    public void updateApplicationResponse(ApplicationResponse applicationResponse) throws SQLException {
        String sql = "UPDATE application_responses SET admin_id = ?, application_id = ?, content = ?, status = ?, modified_at = ? WHERE application_response_id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);

        try {
            statement.setLong(1, applicationResponse.getAdminId());
            statement.setLong(2, applicationResponse.getApplicationId());
            statement.setString(3, applicationResponse.getContent());
            statement.setString(4, applicationResponse.getStatus().name());
            statement.setTimestamp(5, Timestamp.valueOf(applicationResponse.getModifiedAt()));
            statement.setLong(6, applicationResponse.getApplicationResponseId());

            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
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

        try  {
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
        }

        finally {
            DatabaseManager.closeResultSet(resultSet);
            DatabaseManager.closeStatement(statement);
        }

        return applicationResponse;
    }
}
