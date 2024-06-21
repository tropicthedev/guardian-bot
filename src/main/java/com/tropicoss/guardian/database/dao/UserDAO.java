package com.tropicoss.guardian.database.dao;

import com.tropicoss.guardian.database.model.User;

import java.util.List;

public interface UserDAO {
    void addUser(User user);
    User getUserById(String userId);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(String userId);
}
