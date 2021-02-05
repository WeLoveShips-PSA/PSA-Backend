package com.example.PSABackend.controller;

import com.example.PSABackend.classes.User;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequestMapping(path = "all")
@RestController // This means that this class is a Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void addUser(@Valid @NonNull @RequestBody User user) {
        userService.addUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "{id}")
    public User getUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id)
                .orElse(null);
    }

    @DeleteMapping(path = "{id}")
    public void deleteUserByID(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
    }

    @PutMapping(path = "{id}")
    public void updateUser(@PathVariable("id") UUID id,@Valid @NonNull @RequestBody User userToUpdate) {
        userService.updateUser(id, userToUpdate);
    }
}
