package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.ApplicationResponse;

import java.sql.SQLException;
import java.util.List;

public interface ApplicationResponseDAO {
    // void addApplicationResponse(ApplicationResponse applicationResponse) throws SQLException;
    ApplicationResponse getApplicationResponseById(int applicationResponseId) throws SQLException;

    List<ApplicationResponse> getAllApplicationResponses();

    void upsertApplicationResponse(ApplicationResponse applicationResponse) throws SQLException;

    void deleteApplicationResponse(int applicationResponseId) throws SQLException;

    ApplicationResponse getApplicationResponseByApplicationId(long applicationId) throws SQLException;

    ApplicationResponse getApplicationResponseByMessageId(long messageId) throws SQLException;

    void resetApplication(long applicationId) throws SQLException;
}
