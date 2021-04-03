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
    private String email;

    @NotBlank
    private boolean btrDtAlert = false;

    @NotBlank
    private boolean berthNAlert = false;
    @NotBlank
    private boolean statusAlert = false;
    @NotBlank
    private boolean avgSpeedAlert = true;
    @NotBlank
    private boolean distanceToGoAlert = true;
    @NotBlank
    private boolean maxSpeedAlert = false;
    @NotBlank
    private boolean emailOptIn = true;

    public User(@JsonProperty("password") String password, @JsonProperty("user_name") String user_name, @JsonProperty("email") String email) {
        this.password = password;
        this.user_name = user_name;
        this.email = email;
    }


    public User(@NotBlank String password, @NotBlank String user_name, @NotBlank String email, @NotBlank boolean emailOptIn, @NotBlank boolean btrDtAlert, @NotBlank boolean berthNAlert, @NotBlank boolean statusAlert, @NotBlank boolean avgSpeedAlert, @NotBlank boolean distanceToGoAlert, @NotBlank boolean maxSpeedAlert) {
        this.password = password;
        this.user_name = user_name;
        this.email = email;
        this.emailOptIn = emailOptIn;
        this.btrDtAlert = btrDtAlert;
        this.berthNAlert = berthNAlert;
        this.statusAlert = statusAlert;
        this.avgSpeedAlert = avgSpeedAlert;
        this.distanceToGoAlert = distanceToGoAlert;
        this.maxSpeedAlert = maxSpeedAlert;
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

    public void setBtrDtAlert(boolean btrDtAlert) {
        this.btrDtAlert = btrDtAlert;
    }

    public void setBerthNAlert(boolean berthNAlert) {
        this.berthNAlert = berthNAlert;
    }

    public void setStatusAlert(boolean statusAlert) {
        this.statusAlert = statusAlert;
    }

    public void setAvgSpeedAlert(boolean avgSpeedAlert) {
        this.avgSpeedAlert = avgSpeedAlert;
    }

    public void setDistanceToGoAlert(boolean distanceToGoAlert) {
        this.distanceToGoAlert = distanceToGoAlert;
    }

    public void setMaxSpeedAlert(boolean maxSpeedAlert) {
        this.maxSpeedAlert = maxSpeedAlert;
    }

    public boolean isMaxSpeedAlert() {
        return maxSpeedAlert;
    }

    public boolean isDistanceToGoAlert() {
        return distanceToGoAlert;
    }

    public boolean isAvgSpeedAlert() {
        return avgSpeedAlert;
    }

    public boolean isStatusAlert() {
        return statusAlert;
    }

    public boolean isBerthNAlert() {
        return berthNAlert;
    }

    public boolean isBtrDtAlert() {
        return btrDtAlert;
    }

    public boolean isEmailOptIn() {
        return emailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }
}

