package com.tropicoss.guardian.database.dao;

import java.util.List;

public interface MojangAccountDAO {
    void addMojangAccount(MojangAccount mojangAccount);
    MojangAccount getMojangAccountById(int mojangAccountId);
    List<MojangAccount> getAllMojangAccounts();
    void updateMojangAccount(MojangAccount mojangAccount);
    void deleteMojangAccount(int mojangAccountId);
}
