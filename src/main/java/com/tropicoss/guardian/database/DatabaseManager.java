package com.tropicoss.guardian.database;

import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.model.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("guardian.sqlite");
    private final Connection connection;

    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(URL);
        connection.setAutoCommit(false);
    }

    public DatabaseManager(String connectionUrl) throws SQLException {
        connection = DriverManager.getConnection(connectionUrl);
        connection.setAutoCommit(false);
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void runMigrations(String migrationsDir) throws SQLException, IOException {
        MigrationManager migrationManager = new MigrationManager(connection, migrationsDir);
        migrationManager.runMigrations();
    }

    //region Member Methods
    public void addMember(String discordId, String mojangId, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO members (discord_id, mojang_id, is_admin, created_at, modified_at) VALUES (?, ?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, discordId);
            pstmt.setString(2, mojangId);
            pstmt.setInt(3, isAdmin ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Member getMember(String discordId) throws SQLException {
        String sql = "SELECT * FROM members WHERE discord_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, discordId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setDiscordId(rs.getString("discord_id"));
                member.setMojangId(rs.getString("mojang_id"));
                member.setIsAdmin(rs.getInt("is_admin"));
                member.setCreatedAt(rs.getString("created_at"));
                member.setModifiedAt(rs.getString("modified_at"));
                return member;
            } else {
                throw new SQLException("Member not found.");
            }
        }
    }

    public List<Member> getAllMembers() throws SQLException {
        String sql = "SELECT * FROM members";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();

            while (rs.next()) {
                Member member = new Member();
                member.setDiscordId(rs.getString("discord_id"));
                member.setMojangId(rs.getString("mojang_id"));
                member.setIsAdmin(rs.getInt("is_admin"));
                member.setCreatedAt(rs.getString("created_at"));
                member.setModifiedAt(rs.getString("modified_at"));
                members.add(member);
            }
            return members;
        }
    }


    public void updateMember(String discordId, String mojangId, boolean isAdmin) throws SQLException {
        String sql = "UPDATE members SET mojang_id = ?, is_admin = ?, modified_at = datetime('now') WHERE discord_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mojangId);
            pstmt.setInt(2, isAdmin ? 1 : 0);
            pstmt.setString(3, discordId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating member failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteMember(String discordId) throws SQLException {
        String sql = "DELETE FROM members WHERE discord_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, discordId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting member failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Map<String, String> calculateUserStatuses() throws SQLException {
        long gracePeriodDays = Config.getInstance().getConfig().getMember().getInactivityThreshold();

        String sql = "SELECT m.discord_id, m.created_at, " +
                "COALESCE(MAX(COALESCE(s.session_end, s.session_start)), m.created_at) AS last_activity " +
                "FROM members m " +
                "LEFT JOIN sessions s ON m.discord_id = s.discord_id " +
                "GROUP BY m.discord_id, m.created_at";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            Map<String, String> userStatuses = new HashMap<>();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                String discordId = rs.getString("discord_id");
                LocalDate joinDate = rs.getTimestamp("created_at").toLocalDateTime().toLocalDate();
                LocalDate lastActivity = rs.getTimestamp("last_activity").toLocalDateTime().toLocalDate();
                Duration inactivityDuration = Duration.between(lastActivity.atStartOfDay(), today.atStartOfDay());
                long inactivityDays = inactivityDuration.toDays();

                String status;
                if (lastActivity.isAfter(today.minusDays(gracePeriodDays)) && inactivityDays <= gracePeriodDays) {
                    status = "New";
                } else if (inactivityDays <= gracePeriodDays) {
                    status = "Active";
                } else {
                    status = "Inactive";
                }

                userStatuses.put(discordId, status);
            }
            return userStatuses;
        }
    }

    public Map<String, LocalDate> calculatePurgeDates() throws SQLException {
        long gracePeriodDays = Config.getInstance().getConfig().getMember().getInactivityThreshold();

        String sql = "SELECT m.discord_id, " +
                "COALESCE(MAX(COALESCE(s.session_end, s.session_start)), m.created_at) AS last_activity " +
                "FROM members m " +
                "LEFT JOIN sessions s ON m.discord_id = s.discord_id " +
                "GROUP BY m.discord_id, m.created_at";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            Map<String, LocalDate> purgeDates = new HashMap<>();
            LocalDate today = LocalDate.now();

            while (rs.next()) {
                String discordId = rs.getString("discord_id");
                LocalDate lastActivity = rs.getTimestamp("last_activity").toLocalDateTime().toLocalDate();
                LocalDate purgeDate;

                // Determine purge date based on grace period
                if (lastActivity.plusDays(gracePeriodDays).isBefore(today)) {
                    purgeDate = lastActivity.plusDays(gracePeriodDays);
                } else {
                    purgeDate = today.plusDays(gracePeriodDays);
                }

                purgeDates.put(discordId, purgeDate);
            }
            return purgeDates;
        }
    }

    public String getMemberFromChannelId(String channelId) throws SQLException {
        String sql = "SELECT a.discord_id " +
                "FROM interviews i " +
                "JOIN applications a ON i.application_id = a.application_id " +
                "WHERE i.interview_id = ?;";


        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, channelId);
            ResultSet rs = pstmt.executeQuery();
            String discordId = "";

            if (rs.next()) {
                discordId = rs.getString("discord_id");
            } else {
                throw new SQLException("Member not found.");
            }

            return discordId;
        }
    }

    public List<Member> removeInactiveMembers(int daysInactive) throws SQLException {
        List<Member> removedMembers = new ArrayList<>();

        String sqlInsert = "INSERT INTO removed_members (discord_id, mojang_id, login_count, join_date, leave_date)\n" +
                "SELECT m.discord_id, m.mojang_id, COUNT(s.session_id) AS login_count, MIN(m.created_at) AS join_date, datetime('now') AS leave_date\n" +
                "FROM members m\n" +
                "LEFT JOIN sessions s ON m.discord_id = s.discord_id\n" +
                "WHERE (s.session_end IS NULL OR s.session_end < datetime('now', ?))\n" +
                "GROUP BY m.discord_id;";

        String sqlSelect = "SELECT m.discord_id, m.mojang_id, m.is_admin, m.on_vacation, m.created_at, m.modified_at\n" +
                "FROM members m\n" +
                "WHERE m.discord_id IN (\n" +
                "    SELECT discord_id\n" +
                "    FROM removed_members\n" +
                ");";

        String sqlDelete = "DELETE FROM members\n" +
                "WHERE discord_id IN (\n" +
                "    SELECT discord_id\n" +
                "    FROM removed_members\n" +
                ");";

        try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert);
             PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect);
             PreparedStatement pstmtDelete = connection.prepareStatement(sqlDelete)) {

            pstmtInsert.setString(1, "-" + daysInactive + " days");

            pstmtInsert.executeUpdate();

            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setDiscordId(rs.getString("discord_id"));
                member.setMojangId(rs.getString("mojang_id"));
                member.setIsAdmin(rs.getInt("is_admin"));
                member.setOnVacation(rs.getInt("on_vacation"));
                member.setCreatedAt(rs.getString("created_at"));
                member.setModifiedAt(rs.getString("modified_at"));
                removedMembers.add(member);
            }

            pstmtDelete.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        }
        return removedMembers;
    }
    //endregion

    //region Application Methods
    public void addApplication(String applicationId, String content, String discordId) throws SQLException {
        String sql = "INSERT INTO applications (application_id, content, discord_id, created_at, modified_at) VALUES (?, ?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationId);
            pstmt.setString(2, content);
            pstmt.setString(3, discordId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating application failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Application getApplication(String applicationId) throws SQLException {
        String sql = "SELECT * FROM applications WHERE application_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Application application = new Application();
                application.setApplicationId(rs.getString("application_id"));
                application.setContent(rs.getString("content"));
                application.setDiscordId(rs.getString("discord_id"));
                application.setCreatedAt(rs.getString("created_at"));
                application.setModifiedAt(rs.getString("modified_at"));
                return application;
            } else {
                throw new SQLException("Application not found.");
            }
        }
    }

    public List<Application> getAllApplications() throws SQLException {
        String sql = "SELECT * FROM applications";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<Application> applications = new ArrayList<>();

            while (rs.next()) {
                Application application = new Application();
                application.setApplicationId(rs.getString("application_id"));
                application.setContent(rs.getString("content"));
                application.setDiscordId(rs.getString("discord_id"));
                application.setCreatedAt(rs.getString("created_at"));
                application.setModifiedAt(rs.getString("modified_at"));
                applications.add(application);
            }
            return applications;
        }
    }

    public void updateApplication(String applicationId, String newId) throws SQLException {
        String sql = "UPDATE applications SET application_id = ?, modified_at = datetime('now') WHERE application_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, applicationId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating application failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteApplication(String applicationId) throws SQLException {
        String sql = "DELETE FROM applications WHERE application_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting application failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Application getPendingApplicationByDiscordId(String discordId) throws SQLException {
        String sql = "SELECT a.application_id, a.content, a.discord_id, a.created_at\n" +
                "FROM applications a\n" +
                "LEFT JOIN application_responses ar ON a.application_id = ar.application_id\n" +
                "WHERE a.discord_id = ? AND (ar.status IS NULL OR ar.status = 'RESET')\n" +
                "LIMIT 1;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Bind the discordId parameter to the query
            pstmt.setString(1, discordId);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create a map to store the application details
                Application application = new Application();
                application.setApplicationId(rs.getString("application_id"));
                application.setContent(rs.getString("content"));
                application.setDiscordId(rs.getString("discord_id"));
                application.setCreatedAt(rs.getString("created_at"));

                return application;
            } else {
                throw new SQLException("No pending application found for the given Discord ID.");
            }
        }
    }

    public boolean hasPendingApplication(String discordId) throws SQLException {
        String sql = "SELECT 1\n" +
                "FROM applications a\n" +
                "LEFT JOIN application_responses ar ON a.application_id = ar.application_id\n" +
                "WHERE a.discord_id = ? AND (ar.status IS NULL OR ar.status = 'RESET')\n" +
                "LIMIT 1;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, discordId);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
    }

    public void resetApplication(String applicationId) throws SQLException {
        String sql = "UPDATE application_responses " +
                "SET status = 'RESET', modified_at = datetime('now') " +
                "WHERE application_id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Bind the applicationId parameter to the query
            pstmt.setString(1, applicationId);

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No application response found with the given application ID.");
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    //endregion

    //region Interview Methods
    public void addInterview(String interviewId, String applicationId) throws SQLException {
        String sql = "INSERT INTO interviews (interview_id, application_id, created_at, modified_at) VALUES (?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, interviewId);
            pstmt.setString(2, applicationId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating interview failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Interview getInterview(String interviewId) throws SQLException {
        String sql = "SELECT * FROM interviews WHERE interview_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, interviewId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Interview interview = new Interview();
                interview.setInterviewId(rs.getString("interview_id"));
                interview.setApplicationId(rs.getString("application_id"));
                interview.setCreatedAt(rs.getString("created_at"));
                interview.setModifiedAt(rs.getString("modified_at"));
                return interview;
            } else {
                throw new SQLException("Interview not found.");
            }
        }
    }

    public List<Interview> getAllInterviews() throws SQLException {
        String sql = "SELECT * FROM interviews";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<Interview> interviews = new ArrayList<>();

            while (rs.next()) {
                Interview interview = new Interview();
                interview.setInterviewId(rs.getString("interview_id"));
                interview.setApplicationId(rs.getString("application_id"));
                interview.setCreatedAt(rs.getString("created_at"));
                interview.setModifiedAt(rs.getString("modified_at"));
                interviews.add(interview);
            }
            return interviews;
        }
    }


    public void updateInterview(String interviewId, String applicationId) throws SQLException {
        String sql = "UPDATE interviews SET application_id = ?, modified_at = datetime('now') WHERE interview_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationId);
            pstmt.setString(2, interviewId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating interview failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteInterview(String interviewId) throws SQLException {
        String sql = "DELETE FROM interviews WHERE interview_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, interviewId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting interview failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    //endregion

    //region Application Response Methods
    public void addApplicationResponse(String applicationResponseId, String adminId, String applicationId, String content, String status) throws SQLException {
        String sql = "INSERT INTO application_responses (application_response_id, admin_id, application_id, content, status, created_at, modified_at) VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationResponseId);
            pstmt.setString(2, adminId);
            pstmt.setString(3, applicationId);
            pstmt.setString(4, content);
            pstmt.setString(5, status);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating application response failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public ApplicationResponse getApplicationResponse(String applicationResponseId) throws SQLException {
        String sql = "SELECT * FROM application_responses WHERE application_response_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationResponseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ApplicationResponse response = new ApplicationResponse();
                response.setApplicationResponseId(rs.getString("application_response_id"));
                response.setAdminId(rs.getString("admin_id"));
                response.setApplicationId(rs.getString("application_id"));
                response.setContent(rs.getString("content"));
                response.setStatus(rs.getString("status"));
                response.setCreatedAt(rs.getString("created_at"));
                response.setModifiedAt(rs.getString("modified_at"));
                return response;
            } else {
                throw new SQLException("Application response not found.");
            }
        }
    }

    public List<ApplicationResponse> getAllApplicationResponses() throws SQLException {
        String sql = "SELECT * FROM application_responses";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<ApplicationResponse> responses = new ArrayList<>();

            while (rs.next()) {
                ApplicationResponse response = new ApplicationResponse();
                response.setApplicationResponseId(rs.getString("application_response_id"));
                response.setAdminId(rs.getString("admin_id"));
                response.setApplicationId(rs.getString("application_id"));
                response.setContent(rs.getString("content"));
                response.setStatus(rs.getString("status"));
                response.setCreatedAt(rs.getString("created_at"));
                response.setModifiedAt(rs.getString("modified_at"));
                responses.add(response);
            }
            return responses;
        }
    }


    public void updateApplicationResponse(String applicationResponseId, String content, String status) throws SQLException {
        String sql = "UPDATE application_responses SET content = ?, status = ?, modified_at = datetime('now') WHERE application_response_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setString(2, status);
            pstmt.setString(3, applicationResponseId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating application response failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteApplicationResponse(String applicationResponseId) throws SQLException {
        String sql = "DELETE FROM application_responses WHERE application_response_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, applicationResponseId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting application response failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public ApplicationResponse getApplicationResponseByApplicationId(String applicationId) throws SQLException {
        String sql = "SELECT application_response_id, admin_id, application_id, content, status, created_at, modified_at\n" +
                "FROM application_responses\n" +
                "WHERE application_id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Bind the applicationId parameter to the query
            pstmt.setString(1, applicationId);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create a map to store the application response details
                ApplicationResponse response = new ApplicationResponse();
                response.setApplicationResponseId(rs.getString("application_response_id"));
                response.setAdminId(rs.getString("admin_id"));
                response.setApplicationId(rs.getString("application_id"));
                response.setContent(rs.getString("content"));
                response.setStatus(rs.getString("status"));
                response.setCreatedAt(rs.getString("created_at"));
                response.setModifiedAt(rs.getString("modified_at"));

                return response;
            } else {
                throw new SQLException("Application response not found for the given application ID.");
            }
        }
    }

    public void upsertApplicationResponse(String applicationResponseId, String adminId, String applicationId, String content, String status) throws SQLException {
        String sql = "INSERT INTO application_responses (application_response_id, admin_id, application_id, content, status, created_at, modified_at)\n" +
                "VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))\n" +
                "ON CONFLICT(application_id) DO UPDATE SET\n" +
                "admin_id = excluded.admin_id,\n" +
                "content = excluded.content,\n" +
                "status = excluded.status,\n" +
                "modified_at = datetime('now');";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Bind parameters to the prepared statement
            pstmt.setString(1, applicationResponseId);
            pstmt.setString(2, adminId);
            pstmt.setString(3, applicationId);
            pstmt.setString(4, content);
            pstmt.setString(5, status);

            // Execute the query
            pstmt.executeUpdate();
        }
    }

    public ApplicationResponse getApplicationResponseByMessageId(String messageId) throws SQLException {
        String sql = "SELECT application_response_id, admin_id, application_id, content, status, created_at, modified_at\n" +
                "FROM application_responses\n" +
                "WHERE application_id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Bind the messageId (which is application_id) to the query
            pstmt.setString(1, messageId);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create a map to store the application response details
                ApplicationResponse response = new ApplicationResponse();
                response.setApplicationResponseId(rs.getString("application_response_id"));
                response.setAdminId(rs.getString("admin_id"));
                response.setApplicationId(rs.getString("application_id"));
                response.setContent(rs.getString("content"));
                response.setStatus(rs.getString("status"));
                response.setCreatedAt(rs.getString("created_at"));
                response.setModifiedAt(rs.getString("modified_at"));

                return response;
            } else {
                throw new SQLException("No application response found for the given message ID.");
            }
        }
    }
    //endregion

    //region Interview Response Methods
    public void addInterviewResponse(String interviewResponseId, String adminId, String interviewId, String content, String status) throws SQLException {
        String sql = "INSERT INTO interview_responses (interview_response_id, admin_id, interview_id, content, status, created_at, modified_at) VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, interviewResponseId);
            pstmt.setString(2, adminId);
            pstmt.setString(3, interviewId);
            pstmt.setString(4, content);
            pstmt.setString(5, status);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating interview response failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public InterviewResponse getInterviewResponse(String interviewResponseId) throws SQLException {
        String sql = "SELECT * FROM interview_responses WHERE interview_response_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, interviewResponseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                InterviewResponse response = new InterviewResponse();
                response.setInterviewResponseId(rs.getString("interview_response_id"));
                response.setAdminId(rs.getString("admin_id"));
                response.setInterviewId(rs.getString("interview_id"));
                response.setContent(rs.getString("content"));
                response.setStatus(rs.getString("status"));
                response.setCreatedAt(rs.getString("created_at"));
                response.setModifiedAt(rs.getString("modified_at"));
                return response;
            } else {
                throw new SQLException("Interview response not found.");
            }
        }
    }

    public List<InterviewResponse> getAllInterviewResponses() throws SQLException {
        String sql = "SELECT * FROM interview_responses";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<InterviewResponse> responses = new ArrayList<>();

            while (rs.next()) {
                InterviewResponse response = new InterviewResponse();
                response.setInterviewResponseId(rs.getString("interview_response_id"));
                response.setAdminId(rs.getString("admin_id"));
                response.setInterviewId(rs.getString("interview_id"));
                response.setContent(rs.getString("content"));
                response.setStatus(rs.getString("status"));
                response.setCreatedAt(rs.getString("created_at"));
                response.setModifiedAt(rs.getString("modified_at"));
                responses.add(response);
            }
            return responses;
        }
    }


    public void updateInterviewResponse(String interviewResponseId, String content, String status) throws SQLException {
        String sql = "UPDATE interview_responses SET content = ?, status = ?, modified_at = datetime('now') WHERE interview_response_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setString(2, status);
            pstmt.setString(3, interviewResponseId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating interview response failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteInterviewResponse(String interviewResponseId) throws SQLException {
        String sql = "DELETE FROM interview_responses WHERE interview_response_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, interviewResponseId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting interview response failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    //endregion

    //region Server Methods
    public void addServer(String serverId, String name, String token) throws SQLException {
        String sql = "INSERT INTO servers (server_id, name, token, created_at, modified_at) VALUES (?, ?, ?, datetime('now'), datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serverId);
            pstmt.setString(2, name);
            pstmt.setString(3, token);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating server failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Server getServer(String serverId) throws SQLException {
        String sql = "SELECT * FROM servers WHERE server_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serverId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Server server = new Server();
                server.setServerId(rs.getString("server_id"));
                server.setName(rs.getString("name"));
                server.setToken(rs.getString("token"));
                server.setCreatedAt(rs.getString("created_at"));
                server.setModifiedAt(rs.getString("modified_at"));
                return server;
            } else {
                throw new SQLException("Server not found.");
            }
        }
    }

    public List<Server> getAllServers() throws SQLException {
        String sql = "SELECT * FROM servers";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<Server> servers = new ArrayList<>();

            while (rs.next()) {
                Server server = new Server();
                server.setServerId(rs.getString("server_id"));
                server.setName(rs.getString("name"));
                server.setToken(rs.getString("token"));
                server.setCreatedAt(rs.getString("created_at"));
                server.setModifiedAt(rs.getString("modified_at"));
                servers.add(server);
            }
            return servers;
        }
    }


    public void updateServer(String serverId, String name, String token) throws SQLException {
        String sql = "UPDATE servers SET name = ?, token = ?, modified_at = datetime('now') WHERE server_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, token);
            pstmt.setString(3, serverId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating server failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteServer(String serverId) throws SQLException {
        String sql = "DELETE FROM servers WHERE server_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serverId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting server failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    //endregion

    //region Session Methods
    public void addSession(String sessionId, String discordId, String serverId) throws SQLException {
        String sql = "INSERT INTO sessions (session_id, discord_id, server_id, session_start) VALUES (?, ?, ?, datetime('now'))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sessionId);
            pstmt.setString(2, discordId);
            pstmt.setString(3, serverId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating session failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Session getSession(String sessionId) throws SQLException {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sessionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Session session = new Session();
                session.setSessionId(rs.getString("session_id"));
                session.setDiscordId(rs.getString("discord_id"));
                session.setServerId(rs.getString("server_id"));
                session.setSessionStart(rs.getString("session_start"));
                session.setSessionEnd(rs.getString("session_end"));
                return session;
            } else {
                throw new SQLException("Session not found.");
            }
        }
    }

    public List<Session> getAllSessions() throws SQLException {
        String sql = "SELECT * FROM sessions";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            List<Session> sessions = new ArrayList<>();

            while (rs.next()) {
                Session session = new Session();
                session.setSessionId(rs.getString("session_id"));
                session.setDiscordId(rs.getString("discord_id"));
                session.setServerId(rs.getString("server_id"));
                session.setSessionStart(rs.getString("session_start"));
                session.setSessionEnd(rs.getString("session_end"));
                sessions.add(session);
            }
            return sessions;
        }
    }


    public void updateSession(String sessionId, String sessionEnd) throws SQLException {
        String sql = "UPDATE sessions SET session_end = ?, modified_at = datetime('now') WHERE session_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sessionEnd);
            pstmt.setString(2, sessionId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating session failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteSession(String sessionId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE session_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sessionId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting session failed, no rows affected.");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    //endregion
}

