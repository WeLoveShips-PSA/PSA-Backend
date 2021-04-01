package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.service.EmailService;
import com.example.PSABackend.classes.FavAndSubVessel;
import com.example.PSABackend.classes.User;
import com.example.PSABackend.exceptions.DataException;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {
    private static String dbURL;
    private static String username;
    private static String password;

    @Value("${spring.datasource.url}")
    public void setdbURL(String value) {
        this.dbURL = value;
    }

    @Value("${spring.datasource.username}")
    public void setdbUser(String value) {
        this.username = value;
    }

    @Value("${spring.datasource.password}")
    public void setdbPass(String value) {
        this.password = value;
    }

    private UserDAS userDAS = new UserDAS();

    public AlertDAO() {
    }

    public String getMessage(String username, List<Alert> alertList) {
        String out = "Dear " + username + ",\n";
        for (Alert a: alertList) {
            out += a + "\n";
        }
        return out;
    }

    public void getAlerts() throws DataException {
        List<User> userList;
         userList = userDAS.selectAllUsers();

        List<Alert> alertList = new ArrayList<>();
        List<FavAndSubVessel> subbedVesselList = new ArrayList<>();

        if (userList == null) {
            return;
        }

        for (User user : userList) {
            subbedVesselList = userDAS.getSubscribedVesselsPK(user.getUser_name());
            alertList = VesselDAS.detectChangesVessel(user, subbedVesselList);
            insertAlerts(user.getUser_name(), alertList);
            sendAlerts(user, alertList);
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
        String insertAlertQuery = "INSERT INTO alert VALUES(current_timestamp, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(insertAlertQuery);) {
            stmt.setString(1, username);
            for (Alert alert : alertList) {
                stmt.setString(2, alert.getAbbrVslM());
                stmt.setString(3, alert.getInVoyN());
                stmt.setString(4, Double.toString(alert.getNewAvgSpeed()));
                stmt.setString(5, Integer.toString(alert.getNewMaxSpeed()));
                stmt.setString(6, Integer.toString(alert.getNewDistanceToGo()));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                stmt.setString(7, alert.getNewBerthTime().format(formatter));
                stmt.setString(8, alert.getNewBerthNo());
                stmt.setString(9, alert.getNewStatus());
            }

        } catch (Exception e) {

        }
    }
}
