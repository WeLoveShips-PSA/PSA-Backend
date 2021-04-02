
package com.example.PSABackend.controller;

import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "alert")
@RestController
@CrossOrigin
public class AlertController {

    private AlertService alertService = new AlertService();

    @GetMapping
    @RequestMapping(path = "/get")
    public List<Alert> getAlertsByUsername(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        try {
            return alertService.getAlertsByUsername(username);
        } catch (PSAException e) {
            return null;
        }
    }
}
