package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface UserDAO {

    boolean addUser(User user) throws UserAlreadyExistAuthenticationException, InvalidEmailException; // if the user is given an id

    boolean delUser(String username, String password);
//    default boolean addUser(User user) {
//        UUID id = UUID.randomUUID(); // generates our own UUID
//        return add;
//    }

    List<User> selectAllUsers();

    User selectUserById(String username);

//    int deleteUserById(UUID id);
//
//    int updateUserById(UUID id, User user);

    boolean userLogin(String username, String password);

    boolean changeUserPassword(String username, String oldPassword, String newPassword, boolean reset);

    boolean resetUserPassword(String username);

    boolean addFavourite(String username, String abbrVsim, String inVoyn);

    boolean delFavourite(String username, String abbrVsim, String inVoyn);

    ArrayList<Vessel> getFavourite(String username);

    boolean addSubscribed(String username, String abbrVsim, String inVoyn);

    boolean delSubscribed(String username, String abbrVsim, String inVoyn);

    ArrayList<Vessel> getSubscribed(String username);
}
