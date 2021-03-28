package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.User;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class VesselDAS {
    private static String dbURL;
    private static String username;
    private static String password;

    @Value("${spring.datasource.url}")
    public void setdbURL(String value) {
        VesselDAS.dbURL = value;
    }

    @Value("${spring.datasource.username}")
    public void setdbUser(String value) {
        VesselDAS.username = value;
    }

    @Value("${spring.datasource.password}")
    public void setdbPass(String value) {
        VesselDAS.password = value;
    }

    public static ArrayList<VesselDetails> selectAllVessels() {
        ArrayList<VesselDetails> queryList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT fullVslM, vessel.inVoyN inVoyN, outVoyN, btrDt, unbthgDt,berthN, status, avg_speed, is_increasing, max_speed, distance_to_go  FROM VESSEL LEFT OUTER JOIN VESSEL_EXTRA ON VESSEL.ABBRVSLM = VESSEL_EXTRA.ABBRVSLM AND VESSEL.INVOYN = VESSEL_EXTRA.INVOYN";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
//                HashMap<String, String> queryMap = new HashMap<>();
                String fullVslM = rs.getString("fullVslM");
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");
                String outVoyN = rs.getString("outVoyN");
                LocalDateTime bthgDt = rs.getTimestamp("btrDt").toLocalDateTime();
                LocalDateTime unbthgDt = rs.getTimestamp("unbthgDt").toLocalDateTime();
                String berthN = rs.getString("berthN");
                String status = rs.getString("status");
                double avg_speed = rs.getDouble("avg_speed");
                boolean is_increasing = rs.getBoolean("is_increasing");
                int max_speed = rs.getInt("max_speed");
                int distance_to_go = rs.getInt("distance_to_go");
                VesselDetails vesselDetails = new VesselDetails(fullVslM, inVoyN, outVoyN, avg_speed, max_speed, distance_to_go, bthgDt, unbthgDt, berthN, status, is_increasing);
                queryList.add(vesselDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        for(HashMap<String, String> m : queryList){
//            System.out.println(m);
//        }
        return queryList;
    }

    public static Vessel selectVesselById(String abbrVslM, String inVoyN) {
        Vessel vessel = null;
        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = String.format("SELECT * FROM VESSEL WHERE abbrVslM = '%s' AND inVoyN = '%s'", abbrVslM, inVoyN);
            PreparedStatement queryStatement = conn.prepareStatement(query);


            ResultSet rs = queryStatement.executeQuery();

            String fullVslM = null;
            String fullInVoyN = null;
            String outVoyN = null;
            String bthgDt = null;
            String unbthgDt = null;
            String berthN = null;
            String status = null;

            if (rs.next()) {
                fullVslM = rs.getString("fullVslM");
                //            String abbrVslM = rs.getString("abbrVslM");
                //            String inVoyN = rs.getString("inVoyN");
                fullInVoyN = rs.getString("fullInVoyN");
                outVoyN = rs.getString("outVoyN");
                bthgDt = rs.getString("btrDt");
                unbthgDt = rs.getString("unbthgDt");
                berthN = rs.getString("berthN");
                status = rs.getString("status");
            }

            vessel = new Vessel(fullVslM, abbrVslM, inVoyN, fullInVoyN, outVoyN, bthgDt, unbthgDt, berthN, status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vessel;
    }

    public static ArrayList<VesselDetails> getVesselsByDate(LocalDateTime date) {
        ArrayList<VesselDetails> queryList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = String.format("SELECT fullVslM, vessel.inVoyN inVoyN, outVoyN, btrDt, unbthgDt,berthN, status, avg_speed, is_increasing, max_speed, distance_to_go  FROM VESSEL LEFT OUTER JOIN VESSEL_EXTRA ON VESSEL.ABBRVSLM = VESSEL_EXTRA.ABBRVSLM AND VESSEL.INVOYN = VESSEL_EXTRA.INVOYN WHERE date(btrDt) = '%s'", date.toString().split("T")[0]);
//            System.out.println(query);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String fullVslM = rs.getString("fullVslM");
                String inVoyN = rs.getString("inVoyN");
                String outVoyN = rs.getString("outVoyN");
                LocalDateTime bthgDt = rs.getTimestamp("btrDt").toLocalDateTime();
                LocalDateTime unbthgDt = rs.getTimestamp("unbthgDt").toLocalDateTime();
                String berthN = rs.getString("berthN");
                String status = rs.getString("status");
                double avg_speed = rs.getDouble("avg_speed");
                boolean is_increasing = rs.getBoolean("is_increasing");
                int max_speed = rs.getInt("max_speed");
                int distance_to_go = rs.getInt("distance_to_go");
                VesselDetails vesselDetails = new VesselDetails(fullVslM, inVoyN, outVoyN, avg_speed, max_speed, distance_to_go, bthgDt, unbthgDt, berthN, status, is_increasing);
                queryList.add(vesselDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        for(HashMap<String, String> m : queryList){
//            System.out.println(m);
//        }
        return queryList;
    }

    public static void detectChangesVessel() {
        UserDAS userDas = new UserDAS();
        ArrayList<User> allUsers = (ArrayList<User>) userDas.selectAllUsers();
        for (User user : allUsers) {
            try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
                String oldQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
                        "from vessel_log l " +
                        "inner join subscribed_vessel v " +
                        "on v.abbrvslm = l.abbrvslm and v.invoyn = l.invoyn " +
                        "left outer join vessel_extra_log e " +
                        "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
                        "where v.username = ?";
                String newQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
                        "from vessel l " +
                        "inner join subscribed_vessel v " +
                        "on v.abbrvslm = l.abbrvslm and v.invoyn = l.invoyn " +
                        "left outer join vessel_extra e " +
                        "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
                        "where v.username = ?";
                PreparedStatement oldStatement = conn.prepareStatement(oldQuery);
                PreparedStatement newStatement = conn.prepareStatement(newQuery);
                oldStatement.setString(1, user.getUser_name());
                newStatement.setString(1, user.getUser_name());

                ResultSet oldrs = oldStatement.executeQuery();
                ResultSet newrs = newStatement.executeQuery();

                while (oldrs.next()) {
                    while (newrs.next()) {
                        if (newrs.getString("abbrvslm").equals(oldrs.getString("abbrvslm")) &&
                                newrs.getString("invoyn").equals(oldrs.getString("invoyn"))) {

                            if(!(newrs.getString("btrdt").equals(oldrs.getString("btrdt")))){
                                // Write add to alert dao code
                            }
                            if(!(newrs.getString("berthn").equals(oldrs.getString("berthn")))){
                                // Write add to alert dao code
                            }
                            if(!(newrs.getString("status").equals(oldrs.getString("status")))){
                                // Write add to alert dao code
                            }
                            if(!(newrs.getString("avg_speed").equals(oldrs.getString("avg_speed")))){
                                // Write add to alert dao code
                            }
                            if(!(newrs.getString("distance_to_go").equals(oldrs.getString("distance_to_go")))){
                                // Write add to alert dao code
                            }
                            if(!(newrs.getString("max_speed").equals(oldrs.getString("max_speed")))){
                                // Write add to alert dao code
                            }
                            newrs.beforeFirst();
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
