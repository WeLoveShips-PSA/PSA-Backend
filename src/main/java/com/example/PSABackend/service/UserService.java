package com.example.PSABackend.service;

import com.example.PSABackend.DAO.UserDAS;
import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.InvalidEmailException;
import com.example.PSABackend.exceptions.UserAlreadyExistAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserDAS userDAS;

    @Autowired
    public UserService(UserDAS userDAS) { this.userDAS = userDAS; }

    public boolean addUser(User user) throws UserAlreadyExistAuthenticationException, InvalidEmailException {
        return userDAS.addUser(user);
    }

    public boolean delUser(String username, String password) {
        return userDAS.delUser(username, password);
    }

    public boolean delUser(String username, String password) {
        return userDAS.delUser(username, password);
    }

    public List<User> getAllUsers() {
        return userDAS.selectAllUsers();
    }

    public User getUserById(String username) {
        return userDAS.selectUserById(username);
    }

//    public int deleteUser(UUID id) {
//        return userDAS.deleteUserById(id);
//    }

//    public int updateUser(UUID id, User newUser) {
//        return userDAS.updateUserById(id, newUser);
//    }

    public boolean userLogin(String username, String password) { return userDAS.userLogin(username, password); }

    public boolean changeUserPassword(String username, String oldPassword, String newPassword, boolean reset) {
        return userDAS.changeUserPassword(username, oldPassword, newPassword, reset);
    }

    public boolean resetUserPassword(String username) {
        return userDAS.resetUserPassword(username);
    }
    // public int changeUserPassword(UUID id, User newUser) { return userDAS.changeUserPassword(id, newUser); }

    public boolean addFavourite(String username, String abbrVsim, String inVoyn)
    { return userDAS.addFavourite(username, abbrVsim, inVoyn) ; }

    public boolean delFavourite(String username, String abbrVsim, String inVoyn)
    { return userDAS.delFavourite(username, abbrVsim, inVoyn) ; }

    public ArrayList<Vessel> getFavourite(String username, String sort, String order) {
        return userDAS.getFavourite(username, sort, order);
    }

    public boolean addSubscribed(String username, String abbrVsim, String inVoyn)
    { return userDAS.addSubscribed(username, abbrVsim, inVoyn) ; }

    public boolean delSubscribed(String username, String abbrVsim, String inVoyn)
    { return userDAS.delSubscribed(username, abbrVsim, inVoyn) ; }


    public ArrayList<Vessel> getSubscribed(String username, String sort, String order) {
        return userDAS.getSubscribed(username, sort, order);
    }
}
