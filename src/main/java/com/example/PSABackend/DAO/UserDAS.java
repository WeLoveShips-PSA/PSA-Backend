package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.InvalidEmailException;
import com.example.PSABackend.exceptions.UserAlreadyExistAuthenticationException;
import com.example.PSABackend.service.VesselService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class UserDAS {
    private static String dbURL;
    private static String username;
    private static String password;
    private static String allowedEmails;

    @Value("${spring.datasource.url}")
    public void setdbURL(String value) {
        UserDAS.dbURL = value;
    }

    @Value("${spring.datasource.username}")
    public void setdbUser(String value) {
        UserDAS.username = value;
    }

    @Value("${spring.datasource.password}")
    public void setdbPass(String value) {
        UserDAS.password = value;
    }

    @Value("${spring.users.allowed}")
    public void setdbAllowedEmails(String value) {
        UserDAS.allowedEmails = value;
    }

    private static List<User> DB = new ArrayList<User>();

    // UUID id, Integer active, String password, String roles, String user_name, String email
    //@Override
    public boolean addUser(User user) throws UserAlreadyExistAuthenticationException, InvalidEmailException {
        boolean validEmail = false;
        String[] emails = allowedEmails.split(",");
        String userEmail = user.getEmail();

        for (int i = 0; i < emails.length; i++) {
            if (userEmail.endsWith(emails[i])) {
                validEmail = true;
            }
        }

        if (checkUsernameExists(user.getUser_name())) {
            throw new UserAlreadyExistAuthenticationException("Username is already taken.");
        }

        if (checkEmailExists(userEmail)) {
            throw new UserAlreadyExistAuthenticationException("Email is already used.");
        }

        if (!validEmail) {
            throw new InvalidEmailException("Email not allowed.");
        }

        String addUserQuery = "INSERT INTO user VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(this.dbURL,  this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(addUserQuery);) {

            stmt.setString(1, user.getUser_name());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, String.valueOf(user.isBtrDtAlert()).equals("true") ? "0" : "1");
            stmt.setString(5, String.valueOf(user.isBerthNAlert()).equals("true") ? "0" : "1");
            stmt.setString(6, String.valueOf(user.isStatusAlert()).equals("true") ? "0" : "1");
            stmt.setString(7, String.valueOf(user.isAvgSpeedAlert()).equals("true") ? "0" : "1");
            stmt.setString(8, String.valueOf(user.isDistanceToGoAlert()).equals("true") ? "0" : "1");
            stmt.setString(9, String.valueOf(user.isMaxSpeedAlert()).equals("true") ? "0" : "1");
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Not emailer got problem");
            return false;
        }

        try {
            String newUserMessage = "Thank you for joining us";
            Emailer.sendEmail(user.getEmail(), newUserMessage, "Welcome to PSA", user.getUser_name());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Emailer got problem");
            // throw EmailerException or smth
        }
        return true;
    }

    public boolean changeUserConfig(String username, boolean btrDtAlert,boolean berthNAlert, boolean statusAlert, boolean avgSpeedAlert, boolean distanceToGoAlert, boolean maxSpeedAlert) {
        try {
            selectUserById(username);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Username not found. Cannot change user configurations.");
        }

        String changeUserConfigQuery = "UPDATE user SET btrDtAlert = ?, berthNAlert = ?, statusAlert = ?, avgSpeedAlert = ?, distanceToGoAlert = ?, maxSpeedAlert = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL,  this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(changeUserConfigQuery);) {

            stmt.setString(1, String.valueOf(btrDtAlert).equals("true") ? "0" : "1")    ;
            stmt.setString(2, String.valueOf(berthNAlert).equals("true") ? "0" : "1");
            stmt.setString(3, String.valueOf(statusAlert).equals("true") ? "0" : "1");
            stmt.setString(4, String.valueOf(avgSpeedAlert).equals("true") ? "0" : "1");
            stmt.setString(5, String.valueOf(distanceToGoAlert).equals("true") ? "0" : "1");
            stmt.setString(6, String.valueOf(maxSpeedAlert).equals("true") ? "0" : "1");
            stmt.setString(7, username);
            System.out.println(stmt);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    //@Override
    public boolean delUser(String username, String password) {
        if (!userLogin(username, password)) {
            return false;
        }

        String delUserQuery = "DELETE FROM user WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delUserQuery);) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }


    //@Override
    public List<User> selectAllUsers() {
        String getAllUserQuery = "SELECT * FROM USER";
        ArrayList<User> userList = new ArrayList<User>();

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getAllUserQuery);) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String password = rs.getString("password");
                String user_name = rs.getString("username");
                String email = rs.getString("email");

                userList.add(new User(password, user_name, email));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
//        for (User user: userList) {
//            System.out.println(user);
//        }
        return userList;
    }

    //@Override
    public User selectUserById(String username) {
        String getUserQuery = "SELECT * FROM user where username = ?";
        User user = null;

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getUserQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");

                user = new User(password,  name, email);
            } else {
                throw new UsernameNotFoundException(String.format("%s not found", username));
            }

        } catch (SQLException e){
            throw new UsernameNotFoundException(String.format("%s not found", username)); // TODO find out what to do with this
        }
        return user;
    }


    //@Override
    public boolean userLogin(String username, String password) {
        String getPasswordQuery = "SELECT password FROM user where username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
        PreparedStatement stmt = conn.prepareStatement(getPasswordQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String correctPassword = rs.getString("password");
                if (password.equals(correctPassword)) {
                    return true;
                } else {
                    throw new BadCredentialsException("Password is incorrect");
                }
            }
        } catch (SQLException e) {
            throw new UsernameNotFoundException(String.format("%s not found", username));
        }
        return false;
    }

    //@Override
    public boolean changeUserPassword(String username, String oldPassword, String newPassword, boolean reset) {
        if (!reset && !userLogin(username, oldPassword)) {
            return false;
        }

        if (newPassword.length() > 15) {
            return false; // TODO passay password validator
            // TODO throw a passwordvalidationexception
        }

        User oldUser = selectUserById(username);
        if (oldUser == null) { throw new UsernameNotFoundException(String.format("%s not found", username)); } // TODO catch this outside

        oldUser.setPassword(newPassword);

        String changePasswordQuery = "DELETE FROM user WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(changePasswordQuery);) {
            stmt.setString(1, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        try {
            addUser(oldUser);
            return true;
        } catch (UserAlreadyExistAuthenticationException | InvalidEmailException e) {
            System.out.println("Delete query something wrong"); // TODO
            return false;
        }
    }

    //@Override
    public boolean resetUserPassword(String username) {
        String newPassword = RandomStringUtils.randomAlphanumeric(15);
        User user = selectUserById(username);

        String resetPasswordQuery = "UPDATE user SET password = ? where username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(resetPasswordQuery);) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        try {
            String resetPasswordMessage = String.format("Your new password is %s. ", newPassword);
            Emailer.sendEmail(user.getEmail(), resetPasswordMessage, "Request for resetting password", username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // throw EmailerException or smth
        }
        return true;
    }

    public boolean checkUsernameExists(String username) {
        String getEmailListQuery = "SELECT email from user where username = ?";

        boolean usernameExist = false;
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getEmailListQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usernameExist = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return usernameExist;
    }

    public boolean checkEmailExists(String userEmail) {
        String getEmailListQuery = "SELECT email from user where email = ?";

        boolean emailExist = false;
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getEmailListQuery);) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emailExist = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return emailExist;
    }

    //@Override
    public boolean addFavourite(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null) {
            return false;
        }

        String addFavouritesQuery = "INSERT INTO liked_vessel(username, abbrVslM, inVoyN) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
        PreparedStatement stmt = conn.prepareStatement(addFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    //@Override
    public boolean delFavourite(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null){
            return false;
        }

        String delFavouritesQuery = "DELETE FROM liked_vessel WHERE username = ? AND abbrVslM = ? AND inVoyN = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    //@Override
    public ArrayList<Vessel> getFavourite(String username, String sort, String order) {
        ArrayList<FavAndSubVessel> likedVesselsList = new ArrayList<FavAndSubVessel>();
        ArrayList<Vessel> likedList = new ArrayList<Vessel>();
        if (selectUserById(username) == null) {
            return likedList;
        }

        String getFavouriteQuery = "SELECT * FROM liked_vessel WHERE username = ? ORDER BY abbrVslM asc";
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getFavouriteQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");

                likedVesselsList.add(new FavAndSubVessel(abbrVslM, inVoyN));
            }

        } catch (SQLException e) {
            return likedList;
        }
        for (FavAndSubVessel s: likedVesselsList) {
            likedList.add(VesselService.getVesselById(s.getAbbrVslM(), s.getInVoyN()));
        }

        sortVesselList(likedList, sort, order);
        return likedList;
    }

    //@Override
    public boolean addSubscribed(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null){
            return false;
        }

        String addSubscribedQuery = "INSERT INTO subscribed_vessel(username, abbrVslM, inVoyN) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(addSubscribedQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    //@Override
    public boolean delSubscribed(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null){
            return false;
        }

        String delSubscribedQuery = "DELETE FROM subscribed_vessel WHERE username = ? AND abbrVslM = ? AND inVoyN = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delSubscribedQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    //@Override
    public ArrayList<Vessel> getSubscribed(String username, String sort, String order) {
        ArrayList<FavAndSubVessel> subscribedVesselsList = new ArrayList<FavAndSubVessel>();
        ArrayList<Vessel> subscribedList = new ArrayList<Vessel>();
        if (selectUserById(username) == null) {
            return subscribedList;
        }
        String getSubscribedQuery = "SELECT * FROM subscribed_vessel WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getSubscribedQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");

                subscribedVesselsList.add(new FavAndSubVessel(abbrVslM, inVoyN));
            }

        } catch (SQLException e) {
            return subscribedList;
        }

        for (FavAndSubVessel s: subscribedVesselsList) {
            subscribedList.add(VesselService.getVesselById(s.getAbbrVslM(), s.getInVoyN()));
        }
        sortVesselList(subscribedList, sort, order);

        return subscribedList;
    }

    public void sortVesselList(ArrayList<Vessel> list, String sort, String order) {
        Comparator<Vessel> compareByDate = Comparator.comparing(Vessel::getBthgDt).thenComparing(Vessel::getFullVslM);
        Comparator<Vessel> compareByName = Comparator.comparing(Vessel::getFullVslM).thenComparing(Vessel::getBthgDt);

        if (sort.equals("date") && order.equals("asc")) {
            Collections.sort(list, compareByDate);
        } else if (sort.equals("date") && order.equals("desc")) {
            Collections.sort(list, compareByDate.reversed());
        } else if (sort.equals("name") && order.equals("asc")) {
            Collections.sort(list, compareByName);
        } else {
            Collections.sort(list, compareByName.reversed());
        }
    }

    public List<String> getUsernameList() {
        String getUsernameQuery = "SELECT username FROM user";
        List<String> usernameList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getUsernameQuery);) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String username = rs.getString("username");

                usernameList.add(username);
            }

        } catch (SQLException e) {
        }
        return usernameList;
    }

    public List<FavAndSubVessel> getSubscribedVesselsPK(String username) {
        ArrayList<FavAndSubVessel> subscribedVesselsList = new ArrayList<FavAndSubVessel>();
        String getSubscribedQuery = "SELECT * FROM subscribed_vessel WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getSubscribedQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");

                subscribedVesselsList.add(new FavAndSubVessel(abbrVslM, inVoyN));
            }

        } catch (SQLException e) {
        }
        return subscribedVesselsList;
    }
}
