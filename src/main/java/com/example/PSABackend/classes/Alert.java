package com.example.PSABackend.classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Alert {
    private String username;
    private String abbrVslM = "me";
    private double speed = 1;
    private LocalDateTime newETA = LocalDateTime.now();

    public Alert() {

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public LocalDateTime getNewETA() {
        return newETA;
    }

    public void setNewETA(LocalDateTime newETA) {
        this.newETA = newETA;
    }

    public String toString() {
        String alert = "";
        boolean hasChange = false;
        if (newETA != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = newETA.format(formatter);
            alert += "newETA has changed to " + time + ", ";

            hasChange = true;
        }
        if (speed != 0.0) {
            alert += "speed has changed to " + speed + "km/hr, ";
            hasChange = true;
        }
        if (hasChange) {
            alert = alert.substring(0, alert.length() - 2);
            return String.format("Vessel %s's %s.", abbrVslM, alert);
        }
        return null;
    }

    public static void main (String[] args) {
        System.out.println(new Alert());
    }
}
