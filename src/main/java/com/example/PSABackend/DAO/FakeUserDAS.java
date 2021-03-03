package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("pregres")
public class FakeUserDAS implements UserDAO{
    private static String dbURL;
    private static String username;
    private static String password;
    private static String allowedEmails;

    @Value("${spring.datasource.url}")
    public void setdbURL(String value) {
        FakeUserDAS.dbURL = value;
    }

    @Value("${spring.datasource.username}")
    public void setdbUser(String value) {
        FakeUserDAS.username = value;
    }

    @Value("${spring.datasource.password}")
    public void setdbPass(String value) {
        FakeUserDAS.password = value;
    }

    @Value("${spring.users.allowed}")
    public void setdAllowedEmails(String value) {
        FakeUserDAS.allowedEmails = value;
    }

    private static List<User> DB = new ArrayList<User>();

    // UUID id, Integer active, String password, String roles, String user_name, String email
    @Override
    public int addUser(UUID id, User user) {
        boolean validEmail = false;
        String[] emails = allowedEmails.split(",");
        String userEmail = user.getEmail();

        for (int i = 0; i < emails.length; i++) {
            if (userEmail.endsWith(emails[i])) {
                validEmail = true;
            }
        }

        if (checkEmailExists(userEmail)) {
            System.out.println("Email got already, make new one");
            return 0;
        }

        if (!validEmail) {
            System.out.println(user);
            return 0;
        }

        String addUserQuery = "INSERT INTO user(UUiD, name, password, email) VALUES (?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(dbURL, username, password);
             PreparedStatement stmt = conn.prepareStatement(addUserQuery);) {

            stmt.setString(1, id.toString());
            stmt.setString(2, user.getUser_name());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return 0;
        }
        return 1;
    }

    @Override
    public List<User> selectAllUsers() {
        String getAllUserQuery = "SELECT * FROM USER";
        ArrayList<User> userList = new ArrayList<User>();

        try (Connection conn = DriverManager.getConnection(dbURL, username, password);
             PreparedStatement stmt = conn.prepareStatement(getAllUserQuery);) {
            ResultSet rs = stmt.executeQuery(getAllUserQuery);
            while(rs.next()) {
                UUID id = UUID.fromString(rs.getString("UUID"));
                String password = rs.getString("password");
                String user_name = rs.getString("name");
                String email = rs.getString("email");

                userList.add(new User(id, password, "", user_name, email));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        for (User user: userList) {
            System.out.println(user);
        }
        return userList;
    }

    @Override
    public Optional<User> selectUserById(UUID id) {
        return DB.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public int deleteUserById(UUID id) {
        Optional<User> userToDelete = selectUserById(id);
        if (userToDelete.isEmpty()) {
            return 0;
        }
        DB.remove((userToDelete.get()));
        return 1;
    }

    @Override
    //TODO
    public int updateUserById(UUID id, User newUser) {
        return selectUserById(id)
                .map(user -> {
                    int indexOfUserToUpdate= DB.indexOf(user);
                    if (indexOfUserToUpdate >= 0) { // means we got a user to delete
                        DB.set(indexOfUserToUpdate, new User(id, newUser.getPassword(), newUser.getRoles(), newUser.getUser_name(), newUser.getEmail()));
                        return 1;
                    }
                    return 0;
                })
                .orElse(0);
    }

    @Override
    public boolean userLogin(String username, String password) {
        String getPasswordQuery = String.format("SELECT password FROM user where name = '%s'", username);

        try (Connection conn = DriverManager.getConnection(dbURL, username, password);
        PreparedStatement stmt = conn.prepareStatement(getPasswordQuery);) {
            ResultSet rs = stmt.executeQuery(getPasswordQuery);
            if (rs.next()) {
                String correctPassword = rs.getString("password");
                if (password == correctPassword) {
                    return true;
                } else {
                    System.out.println("wrong password");
                    return false;
                }
            } else {
                System.out.println("no account with username");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean checkEmailExists(String userEmail) {
        String getEmailListQuery = String.format("SELECT email from user where email = '%s'", userEmail);

        boolean emailExist = false;
        try (Connection conn = DriverManager.getConnection(dbURL, username, password);
             PreparedStatement stmt = conn.prepareStatement(getEmailListQuery);) {
            ResultSet rs = stmt.executeQuery(getEmailListQuery);
            if (rs != null) {
                emailExist = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return emailExist;
    }
}
