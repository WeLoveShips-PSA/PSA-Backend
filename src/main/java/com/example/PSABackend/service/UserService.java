package com.example.PSABackend.service;

import com.example.PSABackend.DAO.UserDAS;
import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static String allowedEmails;

    private final UserDAS userDAS;

    @Value("${spring.users.allowed}")
    public void setdbAllowedEmails(String value) {
        UserService.allowedEmails = value;
    }

    @Autowired
    public UserService(UserDAS userDAS) { this.userDAS = userDAS; }




    public boolean addUser(User user) throws LoginException, DataException {
        boolean validEmail = false;
        String[] emails = allowedEmails.split(",");
        String userEmail = user.getEmail();

        for (int i = 0; i < emails.length; i++) {
            if (userEmail.endsWith(emails[i])) {
                validEmail = true;
            }
        }

        if (!validEmail) {
            throw new LoginException("Email not allowed.");
        }

        if (checkUsernameExists(user.getUser_name())) {
            throw new LoginException("Username is already taken.");
        }

        if (checkEmailExists(userEmail)) {
            throw new LoginException("Email is already used.");
        }

        if (user.getPassword().length() > 15 || user.getPassword().length() < 8) {
            throw new LoginException("Password is not between 8 to 15 characters");
        }

        if (userDAS.addUser(user)) {
            try {
                String newUserMessage = "Thank you for joining us";
                EmailService.sendEmail(user.getEmail(), newUserMessage, "Welcome to PSA", user.getUser_name());
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                // throw EmailerException or smth
            }
        }
        return false;
    }

    public boolean checkUsernameExists(String username) {
        return userDAS.checkUsernameExists(username);
    }

    public boolean checkEmailExists(String email) {
        return userDAS.checkEmailExists(email);
    }

    public boolean changeUserConfig(String username,boolean emailOptIn, boolean btrDtAlert,boolean berthNAlert, boolean statusAlert, boolean avgSpeedAlert, boolean distanceToGoAlert, boolean maxSpeedAlert) throws DataException {
        try {
            getUserById(username);
        } catch (DataException e) {
            // throw new UsernameNotFoundException("Username not found. Cannot change user configurations.");
        }
        return userDAS.changeUserConfig(username,emailOptIn, btrDtAlert, berthNAlert, statusAlert, avgSpeedAlert, distanceToGoAlert, maxSpeedAlert);
    }
    public boolean delUser(String username, String password) throws DataException, LoginException {
        if (userLogin(username, password) == null) {
            return false;
        }
        return userDAS.delUser(username, password);
    }

    public List<User> getAllUsers() throws DataException {
        return userDAS.selectAllUsers();
    }

    public User getUserById(String username) throws DataException {
        return userDAS.selectUserById(username);
    }

    public User userLogin(String username, String password) throws LoginException, DataException { return userDAS.userLogin(username, password); }

    public boolean changeUserPassword(String username, String oldPassword, String newPassword, boolean reset) throws LoginException, DataException {
        if (!reset && userLogin(username, oldPassword) == null) {
            throw new LoginException("Invalid username or password");
        }

        if (newPassword.length() > 15 || newPassword.length() < 8) {
            throw new LoginException("Password is not between 8 to 15 characters");
        }

        return userDAS.changeUserPassword(username, newPassword);
    }

    public boolean resetUserPassword(String username) throws DataException {
        String newPassword = RandomStringUtils.randomAlphanumeric(15);
        User user = getUserById(username);

        if (!userDAS.changeUserPassword(username, newPassword)) {
            return false;
        }

        try {
            String resetPasswordMessage = String.format("Your new password is %s. ", newPassword);
            EmailService.sendEmail(user.getEmail(), resetPasswordMessage, "Request for resetting password", username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO throw EmailerException or smth
        }
        return true;
    }
    // public int changeUserPassword(UUID id, User newUser) { return userDAS.changeUserPassword(id, newUser); }

    public boolean addFavourite(String username, String abbrVsim, String inVoyn) throws DataException, LoginException
    {         if (getUserById(username) == null) {
        return false;
    }
        return userDAS.addFavourite(username, abbrVsim, inVoyn) ; }

    public boolean delFavourite(String username, String abbrVsim, String inVoyn) throws DataException
    {
        if (getUserById(username) == null){
            return false;
        }
        return userDAS.delFavourite(username, abbrVsim, inVoyn) ; }


    public boolean addSubscribed(String username, String abbrVsim, String inVoyn) throws DataException
    {
        if (getUserById(username) == null){
            return false;
        }
        return userDAS.addSubscribed(username, abbrVsim, inVoyn) ; }

    public boolean delSubscribed(String username, String abbrVsim, String inVoyn) throws DataException
    {
        if (getUserById(username) == null){
            return false;
        }
        return userDAS.delSubscribed(username, abbrVsim, inVoyn) ; }


    public ArrayList<VesselDetails> getSubscribed(String username, String sort, String order) throws DataException {
        if (getUserById(username) == null) {
            return null;
        }
        ArrayList<FavAndSubVessel> subscribedVesselsList = userDAS.getSubscribed(username);
        ArrayList<VesselDetails> subscribedList = new ArrayList<VesselDetails>();

        for (FavAndSubVessel s: subscribedVesselsList) {
            subscribedList.add(VesselService.getVesselById(s.getAbbrVslM(), s.getInVoyN()));
        }
        VesselService.sortVesselList(subscribedList, sort, order);

        return subscribedList;

    }

    public List<FavAndSubVessel> getSubscribedVesselPK(String username) throws DataException {
        return userDAS.getSubscribedVesselsPK(username);
    }


}
