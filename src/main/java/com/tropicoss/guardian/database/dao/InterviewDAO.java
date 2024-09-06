package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.Interview;

import java.sql.SQLException;
import java.util.List;

public interface InterviewDAO {
    void addInterview(Interview interview) throws SQLException;

    Interview getInterviewById(int interviewId) throws SQLException;

    List<Interview> getAllInterviews() throws SQLException;

    void updateInterview(Interview interview) throws SQLException;

    void deleteInterview(int interviewId);
}
