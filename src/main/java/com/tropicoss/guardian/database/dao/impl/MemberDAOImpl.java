package com.tropicoss.guardian.database.dao.impl;

import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.database.dao.MemberDAO;
import com.tropicoss.guardian.database.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberDAOImpl implements MemberDAO {
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
    private static Connection connection = null;

    public MemberDAOImpl(Connection connection) {
        MemberDAOImpl.connection = connection;
    }

    public MemberDAOImpl() throws SQLException {
        connection =  DatabaseManager.getConnection();
    }

    @Override
    public void addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (member_id, discord_id, isAdmin, created_at, modified_at) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, member.getMemberId());
            statement.setString(2, member.getDiscordId());
            statement.setBoolean(3, member.getIsAdmin());
            statement.setTimestamp(4, Timestamp.valueOf(member.getCreatedAt()));
            statement.setTimestamp(5, Timestamp.valueOf(member.getModifiedAt()));

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        finally {
            DatabaseManager.closeStatement(statement);
        }
    }

    @Override
    public Member getMemberById(String memberId) throws SQLException {
        String sql = "SELECT * FROM members WHERE memberId = ?";
        Member member = null;

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, memberId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                member = new Member();
                member.setMemberId(rs.getString("memberId"));
                member.setDiscordId(rs.getString("discordId"));
                member.setIsAdmin(rs.getBoolean("isAdmin"));
                member.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                member.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        }
        finally {
            DatabaseManager.closeResultSet(statement.getResultSet());
            DatabaseManager.closeStatement(statement);
        }

        return member;
    }

    @Override
    public List<Member> getAllMembers() throws SQLException {
        String sql = "SELECT * FROM members";
        List<Member> members = new ArrayList<>();

        Statement statement = null;

        try {

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("memberId"));
                member.setDiscordId(rs.getString("discordId"));
                member.setIsAdmin(rs.getBoolean("isAdmin"));
                member.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                member.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                members.add(member);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        finally {
            DatabaseManager.closeResultSet(statement.getResultSet());
            DatabaseManager.closeStatement(statement);
        }

        return members;
    }

    @Override
    public void updateMember(Member member) {
        String sql = "UPDATE members SET discordId = ?, isAdmin = ?, modifiedAt = ? WHERE memberId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getDiscordId());
            stmt.setBoolean(2, member.getIsAdmin());
            stmt.setTimestamp(3, Timestamp.valueOf(member.getModifiedAt()));
            stmt.setString(4, member.getMemberId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteMember(String memberId) {
        String sql = "DELETE FROM members WHERE memberId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
