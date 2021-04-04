package com.example.PSABackend.controller;

import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.*;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "user")
@CrossOrigin
@RestController // This means that this class is a Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @RequestMapping(path = "/add")
    ResponseEntity<String> addUser(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String email = body.get("email").toString();
        String password = body.get("password").toString();
        String password2 = body.get("password2").toString();
        if (!password.equals(password2)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password does not match.");
        }
        try {
            userService.addUser(new User(password, username, email));
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/config")
    ResponseEntity<String> changeUserConfig(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        boolean emailOptIn = body.get("emailOptIn").toString().equals("true");
        boolean btrDtAlert = body.get("btrDtAlert").toString().equals("true");
        boolean berthNAlert = body.get("berthNAlert").toString().equals("true");
        boolean statusAlert = body.get("statusAlert").toString().equals("true");
        boolean avgSpeedAlert = body.get("avgSpeedAlert").toString().equals("true");
        boolean distanceToGoAlert = body.get("distanceToGoAlert").toString().equals("true");
        boolean maxSpeedAlert = body.get("maxSpeedAlert").toString().equals("true");
        try {
            userService.changeUserConfig(username, emailOptIn, btrDtAlert, berthNAlert, statusAlert, avgSpeedAlert, distanceToGoAlert, maxSpeedAlert);
            return ResponseEntity.status(HttpStatus.OK).body("Configuration Changed Successfully");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            //TODO
        }
    }

    @PostMapping
    @RequestMapping(path = "/del")
    ResponseEntity<String> delUser(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();
        String password2 = body.get("password2").toString();
        if (!password.equals(password2)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password does not match.");
        }
        try {
            if (userService.delUser(username, password)) {
                return ResponseEntity.status(HttpStatus.OK).body("ok");
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("someting wrong ");
            }
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping(path = "/get-all")
    public List<User> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (PSAException e) {
            return null;
        }
    }

    @GetMapping
    @RequestMapping(path = "/get/{username}")
    public ResponseEntity<Object> getUserById(@PathVariable("username") String username) {
        try {
            User user = userService.getUserById(username);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // no user found
            //TODO check return type if can return object
        }
    }

    @PostMapping
    @RequestMapping(path = "/login")
    ResponseEntity<User> userLogin(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();

        try {
            User user = userService.userLogin(username, password);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping
    @RequestMapping(path = "/change-password")
    ResponseEntity<String> changeUserPassword(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String oldPassword = body.get("oldPassword").toString();
        String newPassword = body.get("newPassword").toString();
        System.out.println(username + " " + oldPassword + " " + newPassword);

        try {
            userService.changeUserPassword(username, oldPassword, newPassword, false);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "reset-password")
    ResponseEntity<String> resetUserPassword(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        try {
            userService.resetUserPassword(username);
            return ResponseEntity.status(HttpStatus.OK).body("New Password has be sent to your email");
        } catch (PSAException e) { // catch some emailer exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "add-favourite")
    ResponseEntity<String> addFavourite(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String abbrVsim = body.get("abbrVslM").toString();
        String inVoyn = body.get("inVoyN").toString();
        try {
            userService.addFavourite(username, abbrVsim, inVoyn);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "del-favourite")
    ResponseEntity<String> delFavourite(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String abbrVsim = body.get("abbrVslM").toString();
        String inVoyn = body.get("inVoyN").toString();

        try {
            userService.delFavourite(username, abbrVsim, inVoyn);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "get-favourite")
    //TODO can return Obejct?
    public List<Vessel> getFavourite(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String sort = body.get("sort_by").toString(); // date
        String order = body.get("order").toString(); // asc
        try {
            return userService.getFavourite(username, sort, order);
        } catch (PSAException e) {
            return null;
        }
    }

    @PostMapping
    @RequestMapping(path = "add-subscribed")
    ResponseEntity<String> addSubscribed(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String abbrVsim = body.get("abbrVslM").toString();
        String inVoyn = body.get("inVoyN").toString();

        try {
            userService.addSubscribed(username, abbrVsim, inVoyn);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "del-subscribed")
    ResponseEntity<String> delSubscribed(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String abbrVsim = body.get("abbrVslM").toString();
        String inVoyn = body.get("inVoyN").toString();

        try {
            userService.delSubscribed(username, abbrVsim, inVoyn);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping
    @RequestMapping(path = "get-subscribed")
    public List<Vessel> getSubscribed(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String sort = body.get("sort_by").toString(); // date
        String order = body.get("order").toString(); // asc
        try {
            return userService.getSubscribed(username, sort, order);
        } catch (PSAException e) {
            return null;
        }
    }


}
