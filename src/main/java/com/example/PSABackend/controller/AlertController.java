
package com.example.PSABackend.controller;

import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.exceptions.DataException;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.AlertService;
import com.sun.mail.iap.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getAlertsByUsername(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        try {
            List<Alert> alertList = alertService.getAlertsByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body(alertList);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
