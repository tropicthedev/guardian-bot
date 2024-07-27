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
    private static Connection connection = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

    public ApplicationDAOImpl(Connection connection) {
        ApplicationDAOImpl.connection = connection;
    }

    public ApplicationDAOImpl() throws SQLException {
        connection =  DatabaseManager.getConnection();
    }

    @Override
    public int addApplication(Application application) throws SQLException {
        String sql = "INSERT INTO applications (content, message_id, discord_id, created_at, modified_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        int applicationId = -1;

        try {
            statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, application.getContent());
            statement.setString(2, application.getMessageId());
            statement.setString(3, application.getDiscordId());
            statement.setTimestamp(4, Timestamp.valueOf(application.getCreatedAt()));
            statement.setTimestamp(5, Timestamp.valueOf(application.getModifiedAt()));

            statement.executeUpdate();
            connection.commit();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                applicationId = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(generatedKeys);
            DatabaseManager.closeStatement(statement);
        }

        return applicationId;
    }

    @Override
    public Application getApplicationById(int application_id) throws SQLException {
        String sql = "SELECT * FROM applications WHERE application_id = ?";
        Application application = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.prepareStatement(sql);
            statement.setInt(1, application_id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                application = new Application();
                application.setApplicationId(resultSet.getLong("application_id"));
                application.setContent(resultSet.getString("content"));
                application.setMessageId(resultSet.getString("message_id"));
                application.setDiscordId(resultSet.getString("discord_id"));
                application.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                application.setModifiedAt(resultSet.getTimestamp("modified_at").toLocalDateTime());
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        }
        finally {
            DatabaseManager.closeResultSet(resultSet);
            DatabaseManager.closeStatement(statement);
        }

        return application;
    }

    @Override
    public boolean pendingUserApplication(String userId) throws SQLException {
        String sql = "SELECT EXISTS ( " +
                "SELECT 1 FROM applications a " +
                "LEFT JOIN application_responses ar " +
                "ON a.application_id = ar.application_id " +
                "WHERE a.discord_id = ? AND ar.application_id IS NULL) as record_exists";
        boolean exists = false;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt("record_exists") == 1;
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(resultSet);
            DatabaseManager.closeStatement(statement);
        }

        return exists;
    }

    @Override
    public List<Application> getAllApplications() throws SQLException {
        String sql = "SELECT * FROM applications";
        List<Application> applications = new ArrayList<>();

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                Application application = new Application();
                application.setApplicationId(resultSet.getInt("application_id"));
                application.setContent(resultSet.getString("content"));
                application.setMessageId(resultSet.getString("message_id"));
                application.setDiscordId(resultSet.getString("discord_id"));
                application.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                application.setModifiedAt(resultSet.getTimestamp("modified_at").toLocalDateTime());

                applications.add(application);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        }

        finally {
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeResultSet(resultSet);
        }

        return applications;
    }

    @Override
    public void updateApplication(Application application) throws SQLException {
        String sql = "UPDATE applications SET content = ?, message_id = ?, discord_id = ?, modified_at = ? WHERE application_id = ?";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, application.getContent());
            statement.setString(2, application.getMessageId());
            statement.setString(3, application.getDiscordId());
            statement.setTimestamp(4, Timestamp.valueOf(application.getModifiedAt()));

            statement.setLong(5, application.getApplicationId());

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
    public void deleteApplication(int application_id) throws SQLException {
        String sql = "DELETE FROM applications WHERE application_id = ?";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, application_id);
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
}
