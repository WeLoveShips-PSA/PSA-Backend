package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VesselDAO {

    int addUser(UUID id, User user); // if the user is given an id

    default int addUser(User user) {
        UUID id = UUID.randomUUID(); // generates our own UUID
        return addUser(id, user);
    }

    List<User> selectAllUsers();

    Optional<User> selectUserById(UUID id);

    int deleteUserById(UUID id);

    int updateUserById(UUID id, User user);
}
