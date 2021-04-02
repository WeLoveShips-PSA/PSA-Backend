package com.example.PSABackend.service;

import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.DAO.UserDAS;
import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.classes.FavAndSubVessel;
import com.example.PSABackend.classes.User;
import com.example.PSABackend.exceptions.DataException;

import java.util.ArrayList;
import java.util.List;

public class AlertService {
    private UserDAS userDAS = new UserDAS();

    private UserService userService = new UserService(userDAS); //TODO check if we can just use default constructor

    private AlertDAO alertDAO = new AlertDAO();

    public String getMessage(String username, List<Alert> alertList) {
        String message = "Dear " + username + ",\n";
        for (Alert a: alertList) {
            message += a + "\n";
        }
        return message;
    }

    public void getAlerts() throws DataException {
        List<User> userList;
        userList = userService.getAllUsers();

        List<Alert> alertList = new ArrayList<>();
        List<FavAndSubVessel> subbedVesselList = new ArrayList<>();

        if (userList == null) {
            return;
        }

        for (User user : userList) {
            subbedVesselList = userService.getSubscribedVesselPK(user.getUser_name());
            alertList = VesselService.detectVesselChanges(user, subbedVesselList);
            insertAlerts(user.getUser_name(), alertList);
            if (!alertList.isEmpty()) {
                sendAlerts(user, alertList);
            }
        }
    }
    public void sendAlerts(User user, List<Alert> alertList) {
        String alertMessage = getMessage(user.getUser_name(), alertList);
        System.out.println(alertMessage);
        try {
            EmailService.sendEmail(user.getEmail(), alertMessage, "Alerts for your subscribed Vessel", user.getUser_name());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // throw EmailerException or smth
        }
    }

    public void insertAlerts(String username, List<Alert> alertList) {
        alertDAO.insertAlerts(username, alertList);
    }

}
