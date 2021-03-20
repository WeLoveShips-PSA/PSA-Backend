package com.example.PSABackend.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
// @Entity // This tells Hibernate to make a table out of this class
// @Table(name = "users")
public class User {
// @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
//    private final UUID id;
    @NotBlank
    private String password;
    @NotBlank
    private String user_name;
    @NotBlank
    private String email; // email added
    // TODO
//    @NotBlank
//    private Company companyId;
//    @NotBlank
//    private boolean intervalNotiOptIn;
//    @NotBlank
//    private boolean beforeNotiOptIn;
//    @NotBlank
//    private int notificationInterval; // notification every x time
//    @NotBlank
//    private int notificationBefore; // noitfication x hours before ETA

    public User(@JsonProperty("password") String password, @JsonProperty("user_name") String user_name, @JsonProperty("email") String email) {
        this.password = password;
        this.user_name = user_name;
        this.email = email;
//        this.notificationInterval = 1;
//        this.notificationBefore = 3;
//        this.intervalNotiOptIn = true;
//        this.beforeNotiOptIn = true;

        // this.company = new Company()
        // this.company = CompanyDAO.getCompany(companyName)
        // means we assume no companyNames are unique
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {return email; }

    public String toString() {
        return String.format("%s %s %s", password, user_name, email);
    }

//    public int getNotificationInterval() {
//        return notificationInterval;
//    }
//
//    public void setNotificationInterval(int notificationInterval) {
//        this.notificationInterval = notificationInterval;
//    }
//
//    public int getNotificationBefore() {
//        return notificationBefore;
//    }
//
//    public void setNotificationBefore(int notificationBefore) {
//        this.notificationBefore = notificationBefore;
//    }
}

