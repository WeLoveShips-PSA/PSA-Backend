package com.example.PSABackend.classes;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Alert {
    @NotBlank
    private String abbrVslM;
    @NotBlank
    private String inVoyN;
    @NotBlank
    private String alertDateTime;
    private String outVoyN;
    private double newAvgSpeed;
    private int newMaxSpeed;
    private int newDistanceToGo;
    private LocalDateTime newBerthTime;
    private LocalDateTime newUnBerthTime;
    private String newBerthNo;
    private String newStatus;

    public Alert() {
    }

    public String toString() {
        String alert = "";
        boolean hasChange = false;
        if (newBerthTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = newBerthTime.format(formatter);
            alert += "<div>newETA: " + time + "</div>";

            hasChange = true;
        }

        if (newAvgSpeed != 0.0) {
            alert += "<div>Average Speed: " + newAvgSpeed + "km/hr</div>";
            hasChange = true;
        }

        if (newMaxSpeed != 0) {
            alert += "<div>Max Speed: " + newMaxSpeed + "km/hr</div>";
            hasChange = true;
        }

        if (newDistanceToGo != 0) {
            alert += "<div>Distance To Go: " + newDistanceToGo + "km</div> ";
            hasChange = true;
        }

        if (newBerthNo != null) {
            alert += "<div>Berthing Number: " + newBerthNo + "</div>";
            hasChange = true;
        }

        if (newStatus != null) {
            alert += "<div>Status: " + newStatus + "</div>";
            hasChange = true;
        }

        if (hasChange) {
            return String.format("<h4>%s</h4>%s", abbrVslM, alert);
        }
        return null;
    }

    public String getAbbrVslM() {
        return abbrVslM;
    }

    public int getNewMaxSpeed() {
        return newMaxSpeed;
    }

    public int getNewDistanceToGo(){
        return newDistanceToGo;
    }

    public double getNewAvgSpeed() {
        return newAvgSpeed;
    }

    public LocalDateTime getNewBerthTime() {
        return newBerthTime;
    }

    public String getNewBerthNo() {
        return newBerthNo;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getInVoyN() {
        return inVoyN;
    }


    public void setAbbrVslM(String abbrVslM) {
        this.abbrVslM =abbrVslM;
    }

    public void setInVoyN(String inVoyN) { this.inVoyN = inVoyN; }

    public void setOutVoyN(String outVoyN) {
        this.outVoyN = outVoyN;
    }

    public void setNewAvgSpeed(double newAvgSpeed) {
        this.newAvgSpeed = newAvgSpeed;
    }

    public void setNewMaxSpeed(int newMaxSpeed) {
        this.newMaxSpeed = newMaxSpeed;
    }

    public void setNewDistanceToGo(int newDistanceToGo) {
        this.newDistanceToGo = newDistanceToGo;
    }

    public void setNewBerthTime(LocalDateTime newBerthTime) {
        this.newBerthTime = newBerthTime;
    }

    public void setNewUnBerthTime(LocalDateTime newUnBerthTime) {
        this.newUnBerthTime = newUnBerthTime;
    }

    public void setNewBerthNo(String newBerthNo) {
        this.newBerthNo = newBerthNo;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }


    public String getAlertDateTime() {
        return alertDateTime;
    }

    public void setAlertDateTime(String alertDateTime) {
        this.alertDateTime = alertDateTime;
    }
}