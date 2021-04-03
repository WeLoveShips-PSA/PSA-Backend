package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.*;
import com.example.PSABackend.service.EmailService;
import com.example.PSABackend.service.VesselService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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


    private static List<User> DB = new ArrayList<User>();

    // UUID id, Integer active, String password, String roles, String user_name, String email
    //@Override
    public boolean addUser(User user) throws LoginException, DataException {

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
            throw new DataException("Could not access database");
        }
        return true;
    }

    public boolean changeUserConfig(String username,boolean emailOptIn, boolean btrDtAlert,boolean berthNAlert, boolean statusAlert, boolean avgSpeedAlert, boolean distanceToGoAlert, boolean maxSpeedAlert) throws DataException{

        String changeUserConfigQuery = "UPDATE user SET emailOptIn = ?, btrDtAlert = ?, berthNAlert = ?, statusAlert = ?, avgSpeedAlert = ?, distanceToGoAlert = ?, maxSpeedAlert = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL,  this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(changeUserConfigQuery);) {
            stmt.setString(1, String.valueOf(emailOptIn).equals("true") ? "0" : "1") ;
            stmt.setString(2, String.valueOf(btrDtAlert).equals("true") ? "0" : "1") ;
            stmt.setString(3, String.valueOf(berthNAlert).equals("true") ? "0" : "1");
            stmt.setString(4, String.valueOf(statusAlert).equals("true") ? "0" : "1");
            stmt.setString(5, String.valueOf(avgSpeedAlert).equals("true") ? "0" : "1");
            stmt.setString(6, String.valueOf(distanceToGoAlert).equals("true") ? "0" : "1");
            stmt.setString(7, String.valueOf(maxSpeedAlert).equals("true") ? "0" : "1");
            stmt.setString(8, username);
            System.out.println(stmt);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }
        return true;
    }

    //@Override
    public boolean delUser(String username, String password) throws DataException {
        String delUserQuery = "DELETE FROM user WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delUserQuery);) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }
    }


    //@Override
    public List<User> selectAllUsers() throws DataException {
        String getAllUserQuery = "SELECT * FROM USER";
        ArrayList<User> userList = new ArrayList<User>();

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getAllUserQuery);) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String password = rs.getString("password");
                String user_name = rs.getString("username");
                String email = rs.getString("email");
                boolean emailOptIn = rs.getString("emailOptIn").equals("0") ? true : false;
                boolean isBtrDtAlert = rs.getString("btrDtAlert").equals("0") ? true : false;
                boolean isBerthNAlert = rs.getString("berthNAlert").equals("0") ? true : false;
                boolean isStatusAlert = rs.getString("statusAlert").equals("0") ? true : false;
                boolean isAvgSpeedAlert = rs.getString("avgSpeedAlert").equals("0") ? true : false;
                boolean isDistanceToGoAlert = rs.getString("distanceToGoAlert").equals("0") ? true : false;
                boolean isMaxSpeedAlert = rs.getString("btrDtAlert").equals("0") ? true : false;

                userList.add(new User(password, user_name, email, emailOptIn, isBtrDtAlert, isBerthNAlert, isStatusAlert, isAvgSpeedAlert, isDistanceToGoAlert, isMaxSpeedAlert));
            }
        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }
        return userList;
    }

    //@Override
    public User selectUserById(String username) throws  DataException{
        String getUserQuery = "SELECT * FROM user where username = ?";
        User user = null;

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(getUserQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String user_name = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                boolean emailOptIn = rs.getString("emailOptIn").equals("0") ? true : false;
                boolean isBtrDtAlert = rs.getString("btrDtAlert").equals("0") ? true : false;
                boolean isBerthNAlert = rs.getString("berthNAlert").equals("0") ? true : false;
                boolean isStatusAlert = rs.getString("statusAlert").equals("0") ? true : false;
                boolean isAvgSpeedAlert = rs.getString("avgSpeedAlert").equals("0") ? true : false;
                boolean isDistanceToGoAlert = rs.getString("distanceToGoAlert").equals("0") ? true : false;
                boolean isMaxSpeedAlert = rs.getString("btrDtAlert").equals("0") ? true : false;

                user = new User(password, user_name, email, emailOptIn, isBtrDtAlert, isBerthNAlert, isStatusAlert, isAvgSpeedAlert, isDistanceToGoAlert, isMaxSpeedAlert);
            } else {
                throw new DataException(String.format("%s not found", username));
            }

        } catch (SQLException e){
            throw new DataException(String.format("%s not found", username)); // TODO find out what to do with this
        }
        return user;
    }


    //@Override
    public User userLogin(String username, String password) throws LoginException, DataException{
        String getPasswordQuery = "SELECT * FROM user where username = ?";
        User user = null;

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
        PreparedStatement stmt = conn.prepareStatement(getPasswordQuery);) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String user_name = rs.getString("username");
                String correctPassword = rs.getString("password");
                String email = rs.getString("email");
                boolean emailOptIn = rs.getString("emailOptIn").equals("0") ? true : false;
                boolean isBtrDtAlert = rs.getString("btrDtAlert").equals("0") ? true : false;
                boolean isBerthNAlert = rs.getString("berthNAlert").equals("0") ? true : false;
                boolean isStatusAlert = rs.getString("statusAlert").equals("0") ? true : false;
                boolean isAvgSpeedAlert = rs.getString("avgSpeedAlert").equals("0") ? true : false;
                boolean isDistanceToGoAlert = rs.getString("distanceToGoAlert").equals("0") ? true : false;
                boolean isMaxSpeedAlert = rs.getString("btrDtAlert").equals("0") ? true : false;

                if (password.equals(correctPassword)) {
                    user = new User(password, user_name, email, emailOptIn, isBtrDtAlert, isBerthNAlert, isStatusAlert, isAvgSpeedAlert, isDistanceToGoAlert, isMaxSpeedAlert);
                }
            }
        } catch (SQLException e) {
            throw new DataException(String.format("%s not found", username));
        }
        return user;
    }

    //@Override
    public boolean changeUserPassword (String username, String oldPassword, String newPassword, boolean reset) throws LoginException, DataException {

        String changePasswordQuery = "UPDATE user SET password = ? where username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(changePasswordQuery);) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }
        return true;
    }

    //@Override
    public boolean resetUserPassword(String username, String newPassword) throws DataException {

        String resetPasswordQuery = "UPDATE user SET password = ? where username = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(resetPasswordQuery);) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
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
    public boolean addFavourite(String username, String abbrVslM, String inVoyN) throws DataException, LoginException {

        String addFavouritesQuery = "INSERT INTO liked_vessel(username, abbrVslM, inVoyN) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
        PreparedStatement stmt = conn.prepareStatement(addFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }

        return true;
    }

    //@Override
    public boolean delFavourite(String username, String abbrVslM, String inVoyN) throws DataException {

        String delFavouritesQuery = "DELETE FROM liked_vessel WHERE username = ? AND abbrVslM = ? AND inVoyN = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delFavouritesQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }

        return true;
    }

    //@Override
    public ArrayList<FavAndSubVessel> getFavourite(String username) throws DataException {
        ArrayList<FavAndSubVessel> likedVesselsList = new ArrayList<FavAndSubVessel>();

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
            //TODO throw dataexception KIV
            return null;
        }

        return likedVesselsList;
    }

    //@Override
    public boolean addSubscribed(String username, String abbrVslM, String inVoyN) throws DataException {

        String addSubscribedQuery = "INSERT INTO subscribed_vessel(username, abbrVslM, inVoyN) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(addSubscribedQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }

        return true;
    }

    //@Override
    public boolean delSubscribed(String username, String abbrVslM, String inVoyN) throws DataException {
        String delSubscribedQuery = "DELETE FROM subscribed_vessel WHERE username = ? AND abbrVslM = ? AND inVoyN = ?";

        try (Connection conn = DriverManager.getConnection(this.dbURL, this.username, this.password);
             PreparedStatement stmt = conn.prepareStatement(delSubscribedQuery);) {
            stmt.setString(1, username);
            stmt.setString(2, abbrVslM);
            stmt.setString(3, inVoyN);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Could not access database");
        }

        return true;
    }

    //@Override
    public ArrayList<FavAndSubVessel> getSubscribed(String username) throws DataException {
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
            //TODO throw dataexception or smth
            return subscribedVesselsList;
        }

        return subscribedVesselsList;
    }

    public List<String> getUsernameList() throws DataException {
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
            throw new DataException("Could not access database");
        }
        return usernameList;
    }

    public List<FavAndSubVessel> getSubscribedVesselsPK(String username) throws DataException {
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
            throw new DataException("Could not access database");
        }
        return subscribedVesselsList;
    }
}
