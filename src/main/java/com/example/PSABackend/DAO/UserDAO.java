package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.User;

import java.util.List;
import java.util.UUID;

public interface UserDAO {

    boolean addUser(User user); // if the user is given an id

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
}
