package com.tropicoss.guardian.database.dao;

import com.tropicoss.guardian.database.model.Server;

import java.util.List;

public interface ServerDAO {
    void addServer(Server server);

    Server getServerById(int serverId);

    List<Server> getAllServers();

    void updateServer(Server server);

    void deleteServer(int serverId);
}
