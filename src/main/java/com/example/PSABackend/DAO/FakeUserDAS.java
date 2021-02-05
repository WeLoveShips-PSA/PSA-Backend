package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FakeUserDAS implements UserDAO{
    private static List<User> DB = new ArrayList<User>();

    // UUID id, Integer active, String password, String roles, String user_name, String email
    @Override
    public int addUser(UUID id, User user) {
        DB.add(new User(id, user.getPassword(), user.getRoles(), user.getUser_name(), user.getEmail()));
        return 1;
    }

    @Override
    public List<User> selectAllUsers() {
        return DB;
    }

    @Override
    public Optional<User> selectUserById(UUID id) {
        return DB.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public int deleteUserById(UUID id) {
        Optional<User> userToDelete = selectUserById(id);
        if (userToDelete.isEmpty()) {
            return 0;
        }
        DB.remove((userToDelete.get()));
        return 1;
    }

    @Override
    //TODO
    public int updateUserById(UUID id, User newUser) {
        return selectUserById(id)
                .map(user -> {
                    int indexOfUserToUpdate= DB.indexOf(user);
                    if (indexOfUserToUpdate >= 0) { // means we got a user to delete
                        DB.set(indexOfUserToUpdate, new User(id, newUser.getPassword(), newUser.getRoles(), newUser.getUser_name(), newUser.getEmail()));
                        return 1;
                    }
                    return 0;
                })
                .orElse(0);
    }
}
