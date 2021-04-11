package com.example.PSABackend.controller;

import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.*;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/config")
    ResponseEntity<String> changeUserConfig(@RequestBody Map<String, Object> body) {
        String username = body.get("user_name").toString();
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to delete user");
            }
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping(path = "/get-all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> userList = userService.getAllUsers();
            return ResponseEntity.status(HttpStatus.OK).body(userList);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping(path = "/get/{username}")
    public ResponseEntity<?> getUserById(@PathVariable("username") String username) {
        try {
            User user = userService.getUserById(username);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // no user found
        }
    }

    @PostMapping
    @RequestMapping(path = "/login")
    ResponseEntity<?> userLogin(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();

        try {
            User user = userService.userLogin(username, password);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password does not match");
            }
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password does not match");
        }
    }

    @PostMapping
    @RequestMapping(path = "/change-password")
    ResponseEntity<String> changeUserPassword(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String oldPassword = body.get("oldPassword").toString();
        String newPassword = body.get("newPassword").toString();

        try {
            userService.changeUserPassword(username, oldPassword, newPassword, false);
            return ResponseEntity.status(HttpStatus.OK).body("Password Changed Successfully");
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping
    @RequestMapping(path = "get-subscribed")
    public ResponseEntity<?> getSubscribed(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String sort = body.get("sort_by").toString(); // date
        String order = body.get("order").toString(); // asc
        try {
            List<VesselDetails> subscribedList = userService.getSubscribed(username, sort, order);
            return ResponseEntity.status(HttpStatus.OK).body(subscribedList);
        }  catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


}
