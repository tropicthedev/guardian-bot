package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.MojangAccountDAO;
import com.tropicoss.guardian.database.model.MojangAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MojangAccountDAOImpl implements MojangAccountDAO {
    private final DatabaseManager databaseManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

    public MojangAccountDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addMojangAccount(MojangAccount mojangAccount) {
        String sql = "INSERT INTO mojang_accounts (mojangAccountId, memberId, mojangId, createdAt, modifiedAt) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mojangAccount.getMojangAccountId());
            stmt.setInt(2, mojangAccount.getMemberId());
            stmt.setInt(3, mojangAccount.getMojangId());
            stmt.setTimestamp(4, Timestamp.valueOf(mojangAccount.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(mojangAccount.getModifiedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public MojangAccount getMojangAccountById(int mojangAccountId) {
        String sql = "SELECT * FROM mojang_accounts WHERE mojangAccountId = ?";
        MojangAccount mojangAccount = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mojangAccountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                mojangAccount = new MojangAccount();
                mojangAccount.setMojangAccountId(rs.getInt("mojangAccountId"));
                mojangAccount.setMemberId(rs.getInt("memberId"));
                mojangAccount.setMojangId(rs.getInt("mojangId"));
                mojangAccount.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                mojangAccount.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return mojangAccount;
    }

    @Override
    public List<MojangAccount> getAllMojangAccounts() {
        String sql = "SELECT * FROM mojang_accounts";
        List<MojangAccount> mojangAccounts = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MojangAccount mojangAccount = new MojangAccount();
                mojangAccount.setMojangAccountId(rs.getInt("mojangAccountId"));
                mojangAccount.setMemberId(rs.getInt("memberId"));
                mojangAccount.setMojangId(rs.getInt("mojangId"));
                mojangAccount.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                mojangAccount.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                mojangAccounts.add(mojangAccount);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return mojangAccounts;
    }

    @Override
    public void updateMojangAccount(MojangAccount mojangAccount) {
        String sql = "UPDATE mojang_accounts SET memberId = ?, mojangId = ?, modifiedAt = ? WHERE mojangAccountId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mojangAccount.getMemberId());
            stmt.setInt(2, mojangAccount.getMojangId());
            stmt.setTimestamp(3, Timestamp.valueOf(mojangAccount.getModifiedAt()));
            stmt.setInt(4, mojangAccount.getMojangAccountId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteMojangAccount(int mojangAccountId) {
        String sql = "DELETE FROM mojang_accounts WHERE mojangAccountId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mojangAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
