package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.Interview;

import java.util.List;

public interface InterviewDAO {
    void addInterview(Interview interview);
    Interview getInterviewById(int interviewId);
    List<Interview> getAllInterviews();
    void updateInterview(Interview interview);
    void deleteInterview(int interviewId);
}
