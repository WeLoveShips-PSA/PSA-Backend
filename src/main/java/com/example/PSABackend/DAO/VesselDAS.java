package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
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
    public void setdbPass(String value) { VesselDAS.password = value; }

    public static ArrayList<VesselDetails> selectAllVessels(){
        ArrayList<VesselDetails> queryList = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT fullVslM, vessel.inVoyN inVoyN, outVoyN, btrDt, unbthgDt,berthN, status, avg_speed, is_increasing, max_speed, distance_to_go  FROM VESSEL LEFT OUTER JOIN VESSEL_EXTRA ON VESSEL.ABBRVSLM = VESSEL_EXTRA.ABBRVSLM AND VESSEL.INVOYN = VESSEL_EXTRA.INVOYN";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                String fullVslM = rs.getString("fullVslM");
                String inVoyN = rs.getString("inVoyN");
                String outVoyN = rs.getString("outVoyN");
                LocalDateTime bthgDt= rs.getTimestamp("btrDt").toLocalDateTime();
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
        } catch (SQLException e){
            e.printStackTrace();
        }
//        for(HashMap<String, String> m : queryList){
//            System.out.println(m);
//        }
        return queryList;
    }

    public static Vessel selectVesselById(String abbrVslM, String inVoyN){
        Vessel vessel = null;
        try(Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT * FROM VESSEL WHERE (abbrVslM = ? AND inVoyN = ?)";
            PreparedStatement queryStatement = conn.prepareStatement(query);
            queryStatement.setString(1, abbrVslM);
            queryStatement.setString(2, inVoyN);
            System.out.println(queryStatement.toString());

            ResultSet rs = queryStatement.executeQuery();

            String fullVsIM = null;
            String fullInVoyN = null;
            String outVoyN = null;
            String bthgDt = null;
            String unbthgDt = null;
            String berthN = null;
            String status = null;

            if(rs.next()) {
                fullVsIM = rs.getString("fullVsIM");
                //            String abbrVslM = rs.getString("abbrVslM");
                //            String inVoyN = rs.getString("inVoyN");
                fullInVoyN = rs.getString("fullInVoyN");
                outVoyN = rs.getString("outVoyN");
                bthgDt = rs.getString("btrDt");
                unbthgDt = rs.getString("unbthgDt");
                berthN = rs.getString("berthN");
                status = rs.getString("status");
            }

            vessel = new Vessel(fullVsIM, abbrVslM, inVoyN, fullInVoyN, outVoyN, bthgDt, unbthgDt, berthN, status);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return vessel;
    }

    public static ArrayList<VesselDetails> getVesselsByDate(LocalDateTime date){
        ArrayList<VesselDetails> queryList = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = String.format("SELECT fullVslM, vessel.inVoyN inVoyN, outVoyN, btrDt, unbthgDt,berthN, status, avg_speed, is_increasing, max_speed, distance_to_go  FROM VESSEL LEFT OUTER JOIN VESSEL_EXTRA ON VESSEL.ABBRVSLM = VESSEL_EXTRA.ABBRVSLM AND VESSEL.INVOYN = VESSEL_EXTRA.INVOYN WHERE date(btrDt) = '%s'", date.toString().split("T")[0]);
//            System.out.println(query);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                String fullVslM = rs.getString("fullVslM");
                String inVoyN = rs.getString("inVoyN");
                String outVoyN = rs.getString("outVoyN");
                LocalDateTime bthgDt= rs.getTimestamp("btrDt").toLocalDateTime();
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
        } catch (SQLException e){
            e.printStackTrace();
        }
//        for(HashMap<String, String> m : queryList){
//            System.out.println(m);
//        }
        return queryList;
    }
}
