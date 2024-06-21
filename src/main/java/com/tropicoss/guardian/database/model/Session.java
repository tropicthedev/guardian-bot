package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class Session {
    private int sessionId;
    private int memberId;
    private int serverId;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;

    public Session() {}

    public Session(int sessionId, int memberId, int serverId) {
        this.sessionId = sessionId;
        this.memberId = memberId;
        this.serverId = serverId;
        this.sessionStart = LocalDateTime.now();
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setSessionEnd(LocalDateTime sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public void setSessionStart(LocalDateTime sessionStart) {
        this.sessionStart = sessionStart;
    }

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }

    public LocalDateTime getSessionEnd() {
        return sessionEnd;
    }
}
