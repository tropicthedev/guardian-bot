package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class InterviewResponse {
    private long interviewResponseId;
    private long adminId;
    private long interviewId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public InterviewResponse() {
    }

    public InterviewResponse(long adminId, long interviewId, String content, Status status) {
        this.adminId = adminId;
        this.interviewId = interviewId;
        this.content = content;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public long getInterviewResponseId() {
        return interviewResponseId;
    }

    public void setInterviewResponseId(int interviewResponseId) {
        this.interviewResponseId = interviewResponseId;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        this.modifiedAt = LocalDateTime.now();
    }

    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
        this.modifiedAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}


