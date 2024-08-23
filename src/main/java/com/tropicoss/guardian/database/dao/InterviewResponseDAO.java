package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.InterviewResponse;

import java.sql.SQLException;
import java.util.List;

public interface InterviewResponseDAO {
    void addInterviewResponse(InterviewResponse interviewResponse) throws SQLException;

    InterviewResponse getInterviewResponseById(long interviewResponseId) throws SQLException;

    List<InterviewResponse> getAllInterviewResponses() throws SQLException;

    void updateInterviewResponse(InterviewResponse interviewResponse) throws SQLException;

    void deleteInterviewResponse(long interviewResponseId);

    InterviewResponse getInterviewResponseByChannelId(long channelId) throws SQLException;

}
