package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.Session;

import java.util.List;

public interface SessionDAO {
    void addSession(Session session);

    Session getSessionById(int sessionId);

    List<Session> getAllSessions();

    void updateSession(Session session);

    void deleteSession(int sessionId);
}
