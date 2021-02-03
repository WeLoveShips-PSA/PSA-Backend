package com.example.PSABackend.classes;

import javax.persistence.*;




import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
    private Integer active;
    private String password;
    private String roles;
    private String user_name;

    public User(){
    }

    public User(Integer active, String password, String roles, String user_name) {
        this.active = active;
        this.password = password;
        this.roles = roles;
        this.user_name = user_name;
    }

    public Integer getId() {
        return id;
    }

    public Integer getActive() {
        return active;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }

    public String getUser_name() {
        return user_name;
    }
}

