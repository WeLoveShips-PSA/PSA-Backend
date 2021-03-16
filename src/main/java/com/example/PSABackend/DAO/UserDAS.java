package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.*;
import com.example.PSABackend.service.VesselService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("pregres")
public class UserDAS implements UserDAO{
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
    @Override
    public boolean addUser(User user) throws UserAlreadyExistAuthenticationException, InvalidEmailException{
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

        if (checkUsernameExists(user.getUser_name())) {
            throw new UserAlreadyExistAuthenticationException("Username is already taken.");
        }

        if (checkEmailExists(userEmail)) {
            throw new UserAlreadyExistAuthenticationException("Email is already used.");
        }

        if (!validEmail) {
            throw new InvalidEmailException("Email not allowed.");
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
    public boolean delUser(String username, String password) {
        if (!userLogin(username, password)) {
            return false;
        }

        String delUserQuery = String.format("DELETE FROM user WHERE name = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delUserQuery);) {
            stmt.executeUpdate(delUserQuery);
            return true;

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
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

                userList.add(new User(password, user_name, email));
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

                user = new User(password,  name, email);
            } else {
                throw new UsernameNotFoundException(String.format("%s not found", username));
            }

        } catch (SQLException e){
            throw new UsernameNotFoundException(String.format("%s not found", username)); // TODO find out what to do with this
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
                    throw new BadCredentialsException("Password is incorrect");
                }
            }
        } catch (SQLException e) {
            throw new UsernameNotFoundException(String.format("%s not found", username));
        }
        return false;
    }

    @Override
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

        String changePasswordQuery = String.format("DELETE FROM user WHERE name = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(changePasswordQuery);) {
            stmt.executeUpdate(changePasswordQuery);

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

    @Override
    public boolean resetUserPassword(String username) {
        String newPassword = RandomStringUtils.randomAlphanumeric(15);

        if(changeUserPassword(username, "", newPassword, true)) {
            // TO DO
            // if (sendemail) {}
            return true;
        }
        return false;
    }

    public boolean checkUsernameExists(String username) {
        String getEmailListQuery = String.format("SELECT email from user where name = '%s'", username);

        boolean usernameExist = false;
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getEmailListQuery);) {
            ResultSet rs = stmt.executeQuery(getEmailListQuery);
            if (rs.next()) {
                usernameExist = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return usernameExist;
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
    public boolean addFavourite(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null) {
            return false;
        }

        String addFavouritesQuery = String.format("INSERT INTO liked_vessel(username, abbrVslM, inVoyN) VALUES (?,?,?)");

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

    @Override
    public boolean delFavourite(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null){
            return false;
        }

        String delFavouritesQuery = String.format("DELETE FROM liked_vessel WHERE username = ? AND abbrVslM = ? AND inVoyN = ?");

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<Vessel> getFavourite(String username) {
        ArrayList<LikedVessel> likedVesselsList = new ArrayList<LikedVessel>();
        ArrayList<Vessel> likedList = new ArrayList<Vessel>();
        if (selectUserById(username) == null) {
            return likedList;
        }
        String getFavouriteQuery = String.format("SELECT * FROM liked_vessel WHERE username = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getFavouriteQuery);) {
            ResultSet rs = stmt.executeQuery(getFavouriteQuery);

            while(rs.next()) {
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");

                likedVesselsList.add(new LikedVessel(abbrVslM, inVoyN));
            }

        } catch (SQLException e) {
            System.out.println(e);
            return likedList;
        }
        for (LikedVessel s: likedVesselsList) {
            likedList.add(VesselService.getVesselById(s.getAbbrVslM(), s.getInVoyN()));
        }

        return likedList;
    }

    @Override
    public boolean addSubscribed(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null){
            return false;
        }

        String addFavouritesQuery = String.format("INSERT INTO subscribed_vessel(username, abbrVslM, inVoyN) VALUES (?,?,?)");

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(addFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    @Override
    public boolean delSubscribed(String username, String abbrVslM, String inVoyN) {
        if (selectUserById(username) == null){
            return false;
        }

        String delFavouritesQuery = String.format("DELETE FROM subscribed_vessel WHERE username = ? AND abbrVslM = ? AND inVoyN = ?");

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<Vessel> getSubscribed(String username) {
        ArrayList<SubscribedVessel> subscribedVesselsList = new ArrayList<SubscribedVessel>();
        ArrayList<Vessel> subscribedList = new ArrayList<Vessel>();
        if (selectUserById(username) == null) {
            return subscribedList;
        }
        String getFavouriteQuery = String.format("SELECT * FROM subscribed_vessel WHERE username = '%s'", username);

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getFavouriteQuery);) {
            ResultSet rs = stmt.executeQuery(getFavouriteQuery);

            while(rs.next()) {
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");

                subscribedVesselsList.add(new SubscribedVessel(abbrVslM, inVoyN));
            }

        } catch (SQLException e) {
            System.out.println(e);
            return subscribedList;
        }

        for (SubscribedVessel s: subscribedVesselsList) {
            subscribedList.add(VesselService.getVesselById(s.getAbbrVslM(), s.getInVoyN()));
        }

        return subscribedList;
    }
}
