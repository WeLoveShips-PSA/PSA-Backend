package com.example.PSABackend.service;

import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.DAO.UserDAS;
import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.classes.FavAndSubVessel;
import com.example.PSABackend.classes.User;
import com.example.PSABackend.exceptions.DataException;
import com.example.PSABackend.exceptions.PSAException;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertService {
    private UserDAS userDAS = new UserDAS();

    private UserService userService = new UserService(userDAS); //TODO check if we can just use default constructor

    private AlertDAO alertDAO = new AlertDAO();

    public List<Alert> getAlertsByUsername(String username) throws DataException {
        try {
            User user = userService.getUserById(username);
            if (user == null) {
                throw new DataException("No user found");
            }
        } catch (DataException e) {
            throw e;
        }
        return alertDAO.getAlertsByUsername(username);
    }

    public String getMessage(String username, List<Alert> alertList) {
        String message = "<h3>Dear " + username + ", these are the changes for your subscribed vessels</h3>";
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
            if (!user.isEmailOptIn()) {
                continue;
            }
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
        try {
            EmailService.sendEmail(user.getEmail(), alertMessage, "Alerts for your subscribed Vessel", user.getUser_name());
        } catch (Exception e) {

        }
    }

    public void insertAlerts(String username, List<Alert> alertList) throws DataException {
        alertDAO.insertAlerts(username, alertList);
    }

    @Scheduled(cron = "0 0 1 */7 * *")
    public static void deleteExpiredAlerts() {
        try {
            AlertDAO.deleteExpiredAlerts();
        } catch (PSAException e) {

        }
    }

}
