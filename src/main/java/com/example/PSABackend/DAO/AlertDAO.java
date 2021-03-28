package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Alert;

import java.util.ArrayList;

public class AlertDAO {
    private ArrayList<Alert> alertList;

    public AlertDAO() {
        this.alertList = new ArrayList<Alert>();
    }

    public String toString(String username) {
        String out = "";
        for (Alert a: alertList) {
            for (String u: a.getUsername()) {
                if (u.equals(username)) {
                    out += a + "\n";
                    break;
                }
            }
        }
        return out;
    }

    public ArrayList<String> getAlertUSERS(){
        ArrayList<String> usersRequireAlert= new ArrayList<>();
        for (Alert a: alertList) {
            for (String u: a.getUsername()) {

                if(!usersRequireAlert.contains(u)) {
                    usersRequireAlert.add(u);
                }


            }
        }
        return usersRequireAlert;
    }

    public ArrayList<Alert> getList(){
        return alertList;
    }
}
