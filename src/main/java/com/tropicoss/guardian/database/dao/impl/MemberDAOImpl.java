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
    private final DatabaseManager databaseManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");


    public MemberDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void addMember(Member member) {
        String sql = "INSERT INTO members (memberId, discordId, isAdmin, createdAt, modifiedAt) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member.getMemberId());
            stmt.setString(2, member.getDiscordId());
            stmt.setBoolean(3, member.getIsAdmin());
            stmt.setTimestamp(4, Timestamp.valueOf(member.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(member.getModifiedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE memberId = ?";
        Member member = null;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                member = new Member();
                member.setMemberId(rs.getInt("memberId"));
                member.setDiscordId(rs.getString("discordId"));
                member.setIsAdmin(rs.getBoolean("isAdmin"));
                member.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                member.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return member;
    }

    @Override
    public List<Member> getAllMembers() {
        String sql = "SELECT * FROM members";
        List<Member> members = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("memberId"));
                member.setDiscordId(rs.getString("discordId"));
                member.setIsAdmin(rs.getBoolean("isAdmin"));
                member.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                member.setModifiedAt(rs.getTimestamp("modifiedAt").toLocalDateTime());

                members.add(member);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
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
            stmt.setInt(4, member.getMemberId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE memberId = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
