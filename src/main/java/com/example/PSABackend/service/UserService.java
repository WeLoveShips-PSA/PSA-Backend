package com.example.PSABackend.service;

import com.example.PSABackend.DAO.UserDAO;
import com.example.PSABackend.classes.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserDAO userDAO;

    @Autowired
    public UserService(@Qualifier("pregres") UserDAO userDAO) { this.userDAO = userDAO; }

    public int addUser(User user) {
        return userDAO.addUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.selectAllUsers();
    }

    public Optional<User> getUserById(UUID id) {
        return userDAO.selectUserById(id);
    }

    public int deleteUser(UUID id) {
        return userDAO.deleteUserById(id);
    }

    public int updateUser(UUID id, User newUser) {
        return userDAO.updateUserById(id, newUser);
    }
}
