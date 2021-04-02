package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.DataException;
import com.example.PSABackend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

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

    public static ArrayList<VesselDetails> selectAllVessels() throws DataException {
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
                VesselDetails vesselDetails = new VesselDetails(fullVslM, abbrVslM, inVoyN, outVoyN, avg_speed, max_speed, distance_to_go, bthgDt, unbthgDt, berthN, status, is_increasing);
                queryList.add(vesselDetails);
            }
        } catch (SQLException e) {
            throw new DataException("Could not access the database");
        }
        return queryList;
    }

    public static Vessel selectVesselById(String abbrVslM, String inVoyN) throws DataException {
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
            throw new DataException("Could not access the database");
        }
        return vessel;
    }

    public static List<VesselDetails> getVesselByAbbrVslM(String shortAbbrVslM) throws DataException {
        List<VesselDetails> vesselDetailsList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT fullVslM, abbrvslm, vessel.inVoyN inVoyN, outVoyN, btrDt, unbthgDt,berthN, status, avg_speed, is_increasing, max_speed, distance_to_go  FROM VESSEL LEFT OUTER JOIN VESSEL_EXTRA ON VESSEL.ABBRVSLM = VESSEL_EXTRA.ABBRVSLM AND VESSEL.INVOYN = VESSEL_EXTRA.INVOYN where VESSEL.ABBRVSLM like ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, shortAbbrVslM + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String fullVslM = rs.getString("fullVslM");
                String abbrVslM = rs.getString("abbrvslm");
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
                vesselDetailsList.add(new VesselDetails(fullVslM, abbrVslM, inVoyN, outVoyN, avg_speed, max_speed, distance_to_go, bthgDt, unbthgDt, berthN, status, is_increasing));
            }
        } catch (SQLException e) {
            throw new DataException("Could not access the database");
        }
        return vesselDetailsList;
    }

    public static ArrayList<VesselDetails> getVesselsByDate(LocalDateTime date) throws DataException {
        ArrayList<VesselDetails> queryList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = String.format("SELECT vessel.abbrvslm, fullVslM, vessel.inVoyN inVoyN, outVoyN, btrDt, unbthgDt,berthN, status, avg_speed, is_increasing, max_speed, distance_to_go  FROM VESSEL LEFT OUTER JOIN VESSEL_EXTRA ON VESSEL.ABBRVSLM = VESSEL_EXTRA.ABBRVSLM AND VESSEL.INVOYN = VESSEL_EXTRA.INVOYN WHERE date(btrDt) = '%s'", date.toString().split("T")[0]);
//            System.out.println(query);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String fullVslM = rs.getString("fullVslM");
                String abbrVslM = rs.getString("abbrvslm");
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
                VesselDetails vesselDetails = new VesselDetails(fullVslM, abbrVslM, inVoyN, outVoyN, avg_speed, max_speed, distance_to_go, bthgDt, unbthgDt, berthN, status, is_increasing);
                queryList.add(vesselDetails);
            }
        } catch (SQLException e) {
            throw new DataException("Could not access the database");
        }
        return queryList;
    }

//    public static ResultSet getPreviousVesselDetails(String username, FavAndSubVessel subbedVessel) throws DataException {
//        try (Connection conn = DriverManager.getConnection(VesselDAS.dbURL, VesselDAS.username, VesselDAS.password)) {
//            String oldQuery = "select l.fullvslm, l.abbrvslm, l.invoyn, l.fullinvoyn, l.outvoyn, vsl_voy, btrdt, unbthgdt, berthn, status, avg_speed, distance_to_go, max_speed " +
//                    "from vessel_log l " +
//                    "left outer join vessel_extra_log e " +
//                    "on e.abbrvslm = l.abbrvslm " +
//                    "and e.invoyn = l.invoyn " +
//                    "where l.abbrvslm = ? and l.invoyn = ? " +
//                    "order by abbrvslm asc, l.updatedate desc limit 1";
//            PreparedStatement oldStatement = conn.prepareStatement(oldQuery);
//            oldStatement.setString(1, subbedVessel.getAbbrVslM());
//            oldStatement.setString(2, subbedVessel.getInVoyN());
//            ResultSet oldRs = oldStatement.executeQuery();
//            if (oldRs.next()) {
//
//            }
//        } catch (SQLException e) {
//            throw new DataException("Database Error");
//        }
//    }
//
//    public static ResultSet getCurrentVesselDetails(String username, FavAndSubVessel subbedVessel) throws DataException {
//        try (Connection conn = DriverManager.getConnection(VesselDAS.dbURL, VesselDAS.username, VesselDAS.password)) {
//            String newQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
//                    "from vessel l " +
//                    "left outer join vessel_extra e " +
//                    "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
//                    "where l.abbrvslm  = ? and  l.invoyn = ? " +
//                    "order by abbrvslm asc";
//            PreparedStatement newStatement = conn.prepareStatement(newQuery);
//            newStatement.setString(1, subbedVessel.getAbbrVslM());
//            newStatement.setString(2, subbedVessel.getInVoyN());
//            return newStatement.executeQuery();
//        } catch (SQLException e) {
//            throw new DataException("Database Error");
//        }
//    }

    public static List<Alert> detectChangesVessel(User user, List<FavAndSubVessel> subbedVesselList) throws DataException {
        List<Alert> alertList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(VesselDAS.dbURL, VesselDAS.username, VesselDAS.password)) {
            String oldQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
                    "from vessel_log l " +
                    "left outer join vessel_extra_log e " +
                    "on e.abbrvslm = l.abbrvslm " +
                    "and e.invoyn = l.invoyn " +
                    "where l.abbrvslm = ? and l.invoyn = ? " +
                    "order by abbrvslm asc, l.updatedate desc limit 1";
            String newQuery = "select l.abbrvslm, l.invoyn, btrdt, berthn, status, avg_speed, distance_to_go, max_speed " +
                    "from vessel l " +
                    "left outer join vessel_extra e " +
                    "on e.abbrvslm = l.abbrvslm and e.invoyn = l.invoyn " +
                    "where l.abbrvslm  = ? and  l.invoyn = ? " +
                    "order by abbrvslm asc";
            PreparedStatement oldStatement = conn.prepareStatement(oldQuery);
            PreparedStatement newStatement = conn.prepareStatement(newQuery);

            for (FavAndSubVessel vesselPK : subbedVesselList) {


                oldStatement.setString(1, vesselPK.getAbbrVslM());
                oldStatement.setString(2, vesselPK.getInVoyN());
                newStatement.setString(1, vesselPK.getAbbrVslM());
                newStatement.setString(2, vesselPK.getInVoyN());

                ResultSet oldRs = oldStatement.executeQuery();
                ResultSet newRs = newStatement.executeQuery();

                if (oldRs.next() && newRs.next()) {
                    Alert alert = new Alert();
                    alert.setAbbrVslM(vesselPK.getAbbrVslM());
                    alert.setInVoyN(vesselPK.getInVoyN());
                    boolean hasChange = false;
                    if (needAddAlert(newRs, oldRs, "btrdt", user.isBtrDtAlert())) {
                        alert.setNewBerthTime(Timestamp.valueOf(newRs.getString("btrdt")).toLocalDateTime());
                        hasChange = true;
                    }
                    if (needAddAlert(newRs, oldRs, "berthn", user.isBerthNAlert())) {
                        alert.setNewBerthNo(newRs.getString("berthn"));
                        hasChange = true;
                    }
                    if (needAddAlert(newRs, oldRs, "status", user.isStatusAlert())) {
                        alert.setNewStatus(newRs.getString("status"));
                        hasChange = true;
                    }
                    if (needAddAlert(newRs, oldRs, "avg_speed", user.isAvgSpeedAlert())) {
                        alert.setNewAvgSpeed(Double.parseDouble(newRs.getString("avg_speed")));
                        hasChange = true;
                    }
                    if (needAddAlert(newRs, oldRs, "distance_to_go", user.isDistanceToGoAlert())) {
                        alert.setNewDistanceToGo(Integer.parseInt(newRs.getString("distance_to_go")));
                        hasChange = true;
                    }
                    if (needAddAlert(newRs, oldRs, "max_speed", user.isMaxSpeedAlert())) {
                        alert.setNewMaxSpeed(Integer.parseInt(newRs.getString("max_speed")));
                        hasChange = true;
                    }
                    if (hasChange) {
                        alertList.add(alert);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataException("Could not access the database");
        }
        return alertList;
    }

    public static boolean needAddAlert(ResultSet newRs, ResultSet oldRs, String alertAttribute, boolean alertOpt) throws SQLException {
        if (!alertOpt) {
            return false;
        }
        if (newRs.getString(alertAttribute) == null) {
            return false;
        }
        if (newRs.getString(alertAttribute).equals(oldRs.getString(alertAttribute))) {
            return false;
        }
        return true;
    }

    public static List<TreeMap> getVesselSpeedHistory(String vsl_voy) throws DataException {
        List<TreeMap> speedHistory = new ArrayList<>();
        String getVesselSpeedQuery = "SELECT avg_speed, updatedate from vessel_extra_log where vsl_voy = ?";

        try (Connection conn = DriverManager.getConnection(VesselDAS.dbURL,  VesselDAS.username, VesselDAS.password);
             PreparedStatement stmt = conn.prepareStatement(getVesselSpeedQuery);) {
            stmt.setString(1, vsl_voy);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {

                TreeMap<String, String> timeAndSpeed = new TreeMap<>();
                String dateTime = rs.getString("updatedate");
                timeAndSpeed.put("Time", dateTime);

                TreeMap<String, String> speedMap = new TreeMap<>();
                String avgSpeed = rs.getString("avg_speed");
                timeAndSpeed.put("Average Speed", avgSpeed);
                speedHistory.add(timeAndSpeed);

            }
        } catch (SQLException e) {
            throw new DataException("Database Error");
        }
        String getCurrentVesselSpeedQuery = "SELECT avg_speed from vessel_extra where vsl_voy = ?";
        try (Connection conn = DriverManager.getConnection(VesselDAS.dbURL,  VesselDAS.username, VesselDAS.password);
             PreparedStatement stmt = conn.prepareStatement(getVesselSpeedQuery);) {
            stmt.setString(1, vsl_voy);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TreeMap<String, String> timeAndSpeed = new TreeMap<>();
                String dateTime = LocalDateTime.now().withNano(0).toString().replace('T', ' ');
                timeAndSpeed.put("Time", dateTime);

                String avgSpeed = rs.getString("avg_speed");
                timeAndSpeed.put("Average Speed", avgSpeed);
                speedHistory.add(timeAndSpeed);

            }
        } catch (SQLException e) {
            throw new DataException("Database Error");
        }
        return speedHistory;

    }
}
