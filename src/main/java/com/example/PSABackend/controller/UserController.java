package com.example.PSABackend.controller;

import com.example.PSABackend.classes.User;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping(path = "user")
@RestController // This means that this class is a Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @RequestMapping(path = "/add")
    public void addUser(@Valid @NonNull @RequestBody User user) {
        userService.addUser(user);
        System.out.println(userService.getAllUsers().size());
    }

    @GetMapping
    @RequestMapping(path = "/getall")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping
    @RequestMapping(path = "/get/{id}")
    public User getUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id)
                .orElse(null);
    }

    @PutMapping
    @RequestMapping(path = "/upd/{id}")
    public void updateUser(@PathVariable("id") UUID id,@Valid @NonNull @RequestBody User userToUpdate) {
        userService.updateUser(id, userToUpdate);
    }

    @PostMapping
    @RequestMapping(path = "/login")
    public boolean userLogin(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();
        System.out.println( username + " " + password );
        return userService.userLogin(username, password);
    }

//    @PostMapping

//    @RequestMapping(path = "/changepassword")
//    public void changeUserPassword(@PathVariable("id") UUID id,@Valid @NonNull @RequestBody User userToUpdate) {
//        userService.changeUserPassword(id, userToUpdate);
//    }
}
