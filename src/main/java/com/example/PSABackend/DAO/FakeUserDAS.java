package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.LikedVessel;
import com.example.PSABackend.classes.SubscribedVessel;
import com.example.PSABackend.classes.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public void setdbAllowedEmails(String value) {
        FakeUserDAS.allowedEmails = value;
    }

    private static List<User> DB = new ArrayList<User>();

    // UUID id, Integer active, String password, String roles, String user_name, String email
    @Override
    public boolean addUser(User user) {
        boolean validEmail = false;
        String[] emails = allowedEmails.split(",");
        String userEmail = user.getEmail();

        for (int i = 0; i < emails.length; i++) {
            System.out.println(userEmail);
            System.out.println(emails[i]);
            if (userEmail.endsWith(emails[i])) {
                validEmail = true;
            }
        }

        if (checkEmailExists(userEmail)) {
            System.out.println("Email got already, make new one");
            return false;
        }

        if (!validEmail) {
            System.out.println("Email is not allowed");
            return false;
        }

        String addUserQuery = "INSERT INTO user(name, password, email) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(addUserQuery);) {

            stmt.setString(1, user.getUser_name());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }


    @Override
    public List<User> selectAllUsers() {
        String getAllUserQuery = "SELECT * FROM USER";
        ArrayList<User> userList = new ArrayList<User>();

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getAllUserQuery);) {
            ResultSet rs = stmt.executeQuery(getAllUserQuery);
            while(rs.next()) {
                // UUID id = UUID.fromString(rs.getString("UUID"));
                // UUID id = UUID.fromString(rs.getString("UUID"));
                String password = rs.getString("password");
                String user_name = rs.getString("name");
                String email = rs.getString("email");

                userList.add(new User(password, "", user_name, email));
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
    public User selectUserById(String username) {
        String getUserQuery = String.format("SELECT * FROM user where name = '%s'", username);
        User user = null;

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getUserQuery);) {
            ResultSet rs = stmt.executeQuery(getUserQuery);
            if (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                String email = rs.getString("email");

                user = new User(password, "", name, email);
            } else {
                System.out.println("no account with username");
            }
        } catch (SQLException e){
            System.out.println(e);
        }
        return user;
    }

//    @Override
//    public int deleteUserById(UUID id) {
//        Optional<User> userToDelete = selectUserById(id);
//        if (userToDelete.isEmpty()) {
//            return 0;
//        }
//        DB.remove((userToDelete.get()));
//        return 1;
//    }

//    @Override
//    //TODO
//    public int updateUserById(UUID id, User newUser) {
//        return selectUserById(id)
//                .map(user -> {
//                    int indexOfUserToUpdate= DB.indexOf(user);
//                    if (indexOfUserToUpdate >= 0) { // means we got a user to delete
//                        DB.set(indexOfUserToUpdate, new User(id, newUser.getPassword(), newUser.getRoles(), newUser.getUser_name(), newUser.getEmail()));
//                        return 1;
//                    }
//                    return 0;
//                })
//                .orElse(0);
//    }

    @Override
    public boolean userLogin(String username, String password) {
        String getPasswordQuery = String.format("SELECT password FROM user where name = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
        PreparedStatement stmt = conn.prepareStatement(getPasswordQuery);) {
            ResultSet rs = stmt.executeQuery(getPasswordQuery);
            if (rs.next()) {
                String correctPassword = rs.getString("password");
                if (password.equals(correctPassword)) {
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

    @Override
    public boolean changeUserPassword(String username, String oldPassword, String newPassword, boolean reset) {
        if (!reset && !userLogin(username, oldPassword)) {
            return false;
        }

        if (newPassword.length() > 15) {
            return false;
        }

        User oldUser = selectUserById(username);
        if (oldUser == null) { return false; }

        oldUser.setPassword(newPassword);

        String changePasswordQuery = String.format("DELETE FROM user WHERE name = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(changePasswordQuery);) {
            stmt.executeUpdate(changePasswordQuery);

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        return addUser(oldUser);
    }

    @Override
    public boolean resetUserPassword(String username) {
        String newPassword = RandomStringUtils.randomAlphanumeric(15);

        if(changeUserPassword(username, "", newPassword, true)) {
            // TO DO
            System.out.println("New password has been sent to your email");
            return true;
        }
        return false;
    }

    public boolean checkEmailExists(String userEmail) {
        String getEmailListQuery = String.format("SELECT email from user where email = '%s'", userEmail);

        boolean emailExist = false;
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getEmailListQuery);) {
            ResultSet rs = stmt.executeQuery(getEmailListQuery);
            if (rs.next()) {
                System.out.println("Got email");
                emailExist = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return emailExist;
    }

    @Override
    public boolean addFavourite(String username, String abbrVsim, String inVoyn) {
        if (selectUserById(username) == null){
            System.out.println("No such user");
            return false;
        }

        String addFavouritesQuery = String.format("INSERT INTO liked_vessel(username, abbrVsim, inVoyn) VALUES (?,?,?)");

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
        PreparedStatement stmt = conn.prepareStatement(addFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVsim);
            stmt.setString(3, inVoyn);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<LikedVessel> getFavourite(String username) {
        ArrayList<LikedVessel> likedVesselsList = new ArrayList<LikedVessel>();
        if (selectUserById(username) == null) {
            System.out.println("No such user");
            return likedVesselsList;
        }
        String getFavouriteQuery = String.format("SELECT * FROM liked_vessel WHERE username = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getFavouriteQuery);) {
            ResultSet rs = stmt.executeQuery(getFavouriteQuery);

            while(rs.next()) {
                String abbrVsim = rs.getString("abbrVsim");
                String inVoyn = rs.getString("inVoyn");

                likedVesselsList.add(new LikedVessel(abbrVsim, inVoyn));
            }

        } catch (SQLException e) {
            System.out.println(e);
            return likedVesselsList;
        }
        return likedVesselsList;
    }

    @Override
    public boolean addSubscribed(String username, String abbrVsim, String inVoyn) {
        if (selectUserById(username) == null){
            System.out.println("No such user");
            return false;
        }

        String addFavouritesQuery = String.format("INSERT INTO subscribed_vessel(username, abbrVsim, inVoyn) VALUES (?,?,?)");

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(addFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVsim);
            stmt.setString(3, inVoyn);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<SubscribedVessel> getSubscribed(String username) {
        ArrayList<SubscribedVessel> SubscribedVesselsList = new ArrayList<SubscribedVessel>();
        if (selectUserById(username) == null) {
            System.out.println("No such user");
            return SubscribedVesselsList;
        }
        String getFavouriteQuery = String.format("SELECT * FROM subscribed_vessel WHERE username = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getFavouriteQuery);) {
            ResultSet rs = stmt.executeQuery(getFavouriteQuery);

            while(rs.next()) {
                String abbrVsim = rs.getString("abbrVsim");
                String inVoyn = rs.getString("inVoyn");

                SubscribedVesselsList.add(new SubscribedVessel(abbrVsim, inVoyn));
            }

        } catch (SQLException e) {
            System.out.println(e);
            return SubscribedVesselsList;
        }
        return SubscribedVesselsList;
    }
}
