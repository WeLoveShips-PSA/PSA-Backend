package com.example.PSABackend.service;

import com.example.PSABackend.DAO.UserDAO;
import com.example.PSABackend.classes.LikedVessel;
import com.example.PSABackend.classes.SubscribedVessel;
import com.example.PSABackend.classes.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserDAO userDAO;

    @Autowired
    public UserService(@Qualifier("pregres") UserDAO userDAO) { this.userDAO = userDAO; }

    public boolean addUser(User user) {
        return userDAO.addUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.selectAllUsers();
    }

    public User getUserById(String username) {
        return userDAO.selectUserById(username);
    }

//    public int deleteUser(UUID id) {
//        return userDAO.deleteUserById(id);
//    }

//    public int updateUser(UUID id, User newUser) {
//        return userDAO.updateUserById(id, newUser);
//    }

    public boolean userLogin(String username, String password) { return userDAO.userLogin(username, password); }

    public boolean changeUserPassword(String username, String oldPassword, String newPassword, boolean reset) {
        return userDAO.changeUserPassword(username, oldPassword, newPassword, reset);
    }

    public boolean resetUserPassword(String username) {
        return userDAO.resetUserPassword(username);
    }
    // public int changeUserPassword(UUID id, User newUser) { return userDAO.changeUserPassword(id, newUser); }

    public boolean addFavourite(String username, String abbrVsim, String inVoyn)
    { return userDAO.addFavourite(username, abbrVsim, inVoyn) ; }

    public ArrayList<JSONObject> getFavourite(String username) {
        return userDAO.getFavourite(username);
    }

    public boolean addSubscribed(String username, String abbrVsim, String inVoyn)
    { return userDAO.addSubscribed(username, abbrVsim, inVoyn) ; }

    public ArrayList<JSONObject> getSubscribed(String username) {
        return userDAO.getSubscribed(username);
    }
}
