package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.exceptions.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
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

    public AlertDAO() {
    }

    public List<Alert> getAlertsByUsername(String username) throws DataException {
        ArrayList<Alert> alertList = new ArrayList<>();
        String getAlertsQuery = "SELECT * FROM alert where username = ? and date_time between ? and ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getAlertsQuery)) {
            stmt.setString(1, username);
            LocalDate today = LocalDate.now();
            stmt.setString(2, today.minusDays(7).toString());
            stmt.setString(3, today.toString());
            ResultSet rs = stmt.executeQuery();
//            for (Alert alert : alertList) {
//                stmt.setString(2, alert.getAbbrVslM());
//                stmt.setString(3, alert.getInVoyN());
//                stmt.setString(4, Double.toString(alert.getNewAvgSpeed()));
//                stmt.setString(5, Integer.toString(alert.getNewMaxSpeed()));
//                stmt.setString(6, Integer.toString(alert.getNewDistanceToGo()));
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                stmt.setString(7, alert.getNewBerthTime().format(formatter));
//                stmt.setString(8, alert.getNewBerthNo());
//                stmt.setString(9, alert.getNewStatus());
//            }
            while (rs.next()) {
                String datetime = rs.getString("date_time");
                String abbrVslM = rs.getString("abbrVslm");
                String inVoyn = rs.getString("inVoyn");
                String newAvgSpeed = rs.getString("newAvgSpeed");
                String newMaxSpeed = rs.getString("newMaxSpeed");
                String newDistanceToGo = rs.getString("newDistanceToGo");
                String newBerthTime = rs.getString("newBerthTime");
                String newBerthNo = rs.getString("newBerthNo");
                String newStatus = rs.getString("newStatus");
                Alert alert = new Alert();

                alert.setAbbrVslM(abbrVslM);
                alert.setInVoyN(inVoyn);
                alert.setAlertDateTime(datetime);
                if (newAvgSpeed != null) {
                    alert.setNewAvgSpeed(Double.valueOf(newAvgSpeed));
                }
                if (newMaxSpeed != null) {
                    alert.setNewMaxSpeed(Integer.valueOf(newMaxSpeed));
                }
                if (newDistanceToGo != null) {
                    alert.setNewDistanceToGo(Integer.valueOf(newDistanceToGo));
                }
                if (newBerthTime != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime berthTime = LocalDateTime.parse(newBerthTime, formatter);
                    alert.setNewBerthTime(berthTime);
                }
                if (newBerthNo != null) {
                    alert.setNewBerthNo(newBerthNo);
                }
                if (newStatus != null) {
                    alert.setNewStatus(newStatus);
                }
                alertList.add(alert);
            }

        } catch (SQLException e) {
            throw new DataException("Database Error");
        }
        return alertList;

    }

    public void insertAlerts(String username, List<Alert> alertList) throws DataException {
        String insertAlertQuery = "INSERT INTO alert VALUES(current_timestamp, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (Alert alert : alertList) {
            try (Connection conn = DriverManager.getConnection(AlertDAO.dbURL, AlertDAO.username, AlertDAO.password);
                 PreparedStatement stmt = conn.prepareStatement(insertAlertQuery);) {
                stmt.setString(1, username);
                stmt.setString(2, alert.getAbbrVslM());
                stmt.setString(3, alert.getInVoyN());
                stmt.setString(4, Double.toString(alert.getNewAvgSpeed()));
                stmt.setString(5, Integer.toString(alert.getNewMaxSpeed()));
                stmt.setString(6, Integer.toString(alert.getNewDistanceToGo()));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if (alert.getNewBerthTime() != null) {
                    stmt.setString(7, alert.getNewBerthTime().format(formatter));
                } else {
                    stmt.setString(7, null);
                }
                if (alert.getNewBerthNo() != null) {
                    stmt.setString(8, alert.getNewBerthNo());
                } else {
                    stmt.setString(8, null);
                }
                if (alert.getNewStatus() != null) {
                    stmt.setString(9, alert.getNewStatus());
                } else {
                    stmt.setString(9, null);
                }
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataException("Database error");
            }
        }
    }
}
