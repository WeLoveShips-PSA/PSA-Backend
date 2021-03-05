package com.example.PSABackend.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
// @Entity // This tells Hibernate to make a table out of this class
// @Table(name = "users")
public class User {
// @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
//    private final UUID id;
//    @NotBlank
    private String password;
    @NotBlank
    private String roles;
    @NotBlank
    private String user_name;
    @NotBlank
    private String email; // email added
    @NotBlank
    private Company companyId;

    public User(@JsonProperty("password") String password, @JsonProperty("roles") String roles, @JsonProperty("user_name") String user_name, @JsonProperty("email") String email) {
        this.password = password;
        this.roles = roles;
        this.user_name = user_name;
        this.email = email;
        // this.company = new Company()
        // this.company = CompanyDAO.getCompany(companyName)
        // means we assume no companyNames are unique
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getRoles() {
        return roles;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {return email; }

    public String toString() {
        return String.format("%s %s %s", password, user_name, email);
    }
}

