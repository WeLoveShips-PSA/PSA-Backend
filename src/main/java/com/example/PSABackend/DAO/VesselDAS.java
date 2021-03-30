package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.*;
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

    public static List<Vessel> getVesselByAbbrVslM(String shortAbbrVslM) {
        List<Vessel> vesselList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT * FROM VESSEL WHERE abbrVslM like ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, shortAbbrVslM + "%");

            ResultSet rs = stmt.executeQuery();

//            String inVoyN = null;
//            String fullVslM = null;
//            String fullInVoyN = null;
//            String outVoyN = null;
//            String bthgDt = null;
//            String unbthgDt = null;
//            String berthN = null;
//            String status = null;

            while (rs.next()) {
                String fullVslM = rs.getString("fullVslM");
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");
                String fullInVoyN = rs.getString("fullInVoyN");
                String outVoyN = rs.getString("outVoyN");
                String bthgDt = rs.getString("btrDt");
                String unbthgDt = rs.getString("unbthgDt");
                String berthN = rs.getString("berthN");
                String status = rs.getString("status");

                vesselList.add(new Vessel(fullVslM, abbrVslM, inVoyN, fullInVoyN, outVoyN, bthgDt, unbthgDt, berthN, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vesselList;
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

    public static List<Alert> detectChangesVessel(String username, List<FavAndSubVessel> subbedVesselList) {
//        UserDAS userDas = new UserDAS();
//        ArrayList<User> allUsers = (ArrayList<User>) userDas.selectAllUsers();
//        for (User user : allUsers) {
        List<Alert> alertList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(VesselDAS.dbURL, VesselDAS.username, VesselDAS.password)) {
            String oldQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
                    "from vessel_log l " +
                    "left outer join vessel_extra_log e " +
                    "on e.abbrvslm = l.abbrvslm " +
                    "and e.invoyn = l.invoyn " +
                    "where l.abbrvslm = ? and l.invoyn = ? " +
//                "and extract(hour from l.updatedate) = extract(hour from now()) " +
                    "order by abbrvslm asc, l.updatedate desc limit 1";
            String newQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
                    "from vessel l " +
                    "left outer join vessel_extra e " +
                    "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
                    "where l.abbrvslm  = ? and  l.invoyn = ? " +
                    "order by abbrvslm asc";
            PreparedStatement oldStatement = conn.prepareStatement(oldQuery);
            PreparedStatement newStatement = conn.prepareStatement(newQuery);
//            String oldQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
//                    "from vessel_log l " +
//                    "inner join subscribed_vessel v " +
//                    "on v.abbrvslm = l.abbrvslm and v.invoyn = l.invoyn " +
//                    "left outer join vessel_extra_log e " +
//                    "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
//                    "where v.username = ? and " +
//                    "extract(hour from l.updatedate) = extract(hour from now()) " +
//                    "order by abbrvslm asc";
//            String newQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
//                    "from vessel l " +
//                    "inner join subscribed_vessel v " +
//                    "on v.abbrvslm = l.abbrvslm and v.invoyn = l.invoyn " +
//                    "left outer join vessel_extra e " +
//                    "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
//                    "where v.username = ? " +
//                    "order by abbrvslm asc";
//            PreparedStatement oldStatement = conn.prepareStatement(oldQuery);
//            PreparedStatement newStatement = conn.prepareStatement(newQuery);
            for (FavAndSubVessel vesselPK : subbedVesselList) {


                oldStatement.setString(1, vesselPK.getAbbrVslM());
                oldStatement.setString(2, vesselPK.getInVoyN());
                newStatement.setString(1, vesselPK.getAbbrVslM());
                newStatement.setString(2, vesselPK.getInVoyN());

                ResultSet oldrs = oldStatement.executeQuery();
                ResultSet newrs = newStatement.executeQuery();

                if (oldrs.next() && newrs.next()) {
                    Alert alert = new Alert();
                    alert.setAbbrVslM(vesselPK.getAbbrVslM());
                    boolean hasChange = false;
                    if (newrs.getString("btrdt") != null && !(newrs.getString("btrdt").equals(oldrs.getString("btrdt")))) {
                        alert.setNewBerthTime(Timestamp.valueOf(newrs.getString("btrdt")).toLocalDateTime());
                        hasChange = true;
                    }
                    if (newrs.getString("berthn") != null && !(newrs.getString("berthn").equals(oldrs.getString("berthn")))) {
                        alert.setNewBerthNo(newrs.getString("berthn"));
                        hasChange = true;
                    }
                    if (newrs.getString("status") != null && !(newrs.getString("status").equals(oldrs.getString("status")))) {
                        alert.setNewStatus(newrs.getString("status"));
                        hasChange = true;
                    }
                    if (newrs.getString("avg_speed") != null && !(newrs.getString("avg_speed").equals(oldrs.getString("avg_speed")))) {
                        alert.setNewAvgSpeed(Double.parseDouble(newrs.getString("avg_speed")));
                        hasChange = true;
                    }
                    if (newrs.getString("distance_to_go") != null && !(newrs.getString("distance_to_go").equals(oldrs.getString("distance_to_go")))) {
                        alert.setNewDistanceToGo(Integer.parseInt(newrs.getString("distance_to_go")));
                        hasChange = true;
                    }
                    if (newrs.getString("max_speed") != null && !(newrs.getString("max_speed").equals(oldrs.getString("max_speed")))) {
                        alert.setNewMaxSpeed(Integer.parseInt(newrs.getString("max_speed")));
                        hasChange = true;
                    }
                    if (hasChange) {
                        alertList.add(alert);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alertList;
    }
}
