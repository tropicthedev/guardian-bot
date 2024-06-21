package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.InterviewResponse;

import java.util.List;

public interface InterviewResponseDAO {
    void addInterviewResponse(InterviewResponse interviewResponse);
    InterviewResponse getInterviewResponseById(int interviewResponseId);
    List<InterviewResponse> getAllInterviewResponses();
    void updateInterviewResponse(InterviewResponse interviewResponse);
    void deleteInterviewResponse(int interviewResponseId);
}
