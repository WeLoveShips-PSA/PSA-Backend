package com.example.PSABackend.controller;

import com.example.PSABackend.classes.User;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
    @RequestMapping(path = "/get/{username}")
    public User getUserById(@PathVariable("id") String username) {
        return userService.getUserById(username);
    }

//    @PutMapping
//    @RequestMapping(path = "/upd/{id}")
//    public void updateUser(@PathVariable("id") UUID id,@Valid @NonNull @RequestBody User userToUpdate) {
//        userService.updateUser(id, userToUpdate);
//    }

    @PostMapping
    @RequestMapping(path = "/login")
    public boolean userLogin(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();
        System.out.println(username + " " + password);
        return userService.userLogin(username, password);
    }

    @PostMapping
    @RequestMapping(path = "/changepassword")
    public boolean changeUserPassword(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String oldPassword = body.get("oldPassword").toString();
        String newPassword = body.get("newPassword").toString();
        System.out.println(username + " " + oldPassword + " " + newPassword);
        return userService.changeUserPassword(username, oldPassword, newPassword, false);
    }

    @PostMapping
    @RequestMapping(path = "resetpassword")
    public boolean resetUserPassword(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        return userService.resetUserPassword(username);
    }

}
