package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.UserDAO;
import com.tropicoss.guardian.database.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAOImpl implements UserDAO {
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
    private final DatabaseManager databaseManager;


    public UserDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (userId, username, discriminator, accessToken, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getDiscriminator());
            stmt.setString(4, user.getAccessToken());
            stmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(user.getUpdatedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE userId = ?";
        User user = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getString("userId"));
                user.setUsername(rs.getString("username"));
                user.setDiscriminator(rs.getString("discriminator"));
                user.setAccessToken(rs.getString("accessToken"));
                user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("userId"));
                user.setUsername(rs.getString("username"));
                user.setDiscriminator(rs.getString("discriminator"));
                user.setAccessToken(rs.getString("accessToken"));
                user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());

                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return users;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, discriminator = ?, accessToken = ?, updatedAt = ? WHERE userId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getDiscriminator());
            stmt.setString(3, user.getAccessToken());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getUpdatedAt()));
            stmt.setString(5, user.getUserId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE userId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
