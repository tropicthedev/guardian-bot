package com.tropicoss.guardian.database.dao;

import com.tropicoss.guardian.database.model.Application;

import java.sql.SQLException;
import java.util.List;

public interface ApplicationDAO {
    int addApplication(Application application) throws SQLException;
    Application getApplicationById(int applicationId) throws SQLException;
    boolean pendingUserApplication(String userId) throws SQLException;
    List<Application> getAllApplications() throws SQLException;
    void updateApplication(Application application) throws SQLException;
    void deleteApplication(int applicationId) throws SQLException;
    Application getApplicationByMessageId(String messageId) throws SQLException;
    Long getMemberFromChannelId (long id) throws SQLException;
}
