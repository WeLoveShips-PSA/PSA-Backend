package com.example.PSABackend.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;




import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

// @Entity // This tells Hibernate to make a table out of this class
// @Table(name = "users")
public class User {
    // @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
    private UUID id;
    @NotBlank
    private String password;
    @NotBlank
    private String roles;
    @NotBlank
    private String user_name;
    @NotBlank
    private String email; // email added
    //Should add company

    public User(@JsonProperty("id") UUID id, @JsonProperty("password") String password, @JsonProperty("roles") String roles, @JsonProperty("user_name") String user_name, @JsonProperty("email") String email) {
        this.id = id;
        this.password = password;
        this.roles = roles;
        this.user_name = user_name;
        this.email = email;
    }
// private Company company; // need store which company they are from

    public UUID getId() {
        return id;
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

    public String getEmail() {return email; }
}

