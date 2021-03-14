package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Vessel;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//shaun
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

    //database already contains vessel information through PortNetConnectorDAO
    //use vesseldao to retrieve the information from the database
    //make it so that it retrieves ALL vessels from database
    //could refer to fakeuserDAS/companyDAS for reference
    // work in order of vessel->vessel_extra->vesseldao->vesselservice->vesselcontroller

    public static ArrayList<Vessel> selectAllVessels(){
        ArrayList<Vessel> queryList = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs102", "root", "C289cdf456!")) {
            String query = "SELECT * FROM VESSEL";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
//                HashMap<String, String> queryMap = new HashMap<>();
                String fullVsIM = rs.getString("fullVsIM");
                String abbrVslM = rs.getString("abbrVslM");
                String inVoyN = rs.getString("inVoyN");
                String fullInVoyN = rs.getString("fullInVoyN");
                String outVoyN = rs.getString("outVoyN");
                String bthgDt= rs.getString("btrDt");
                String unbthgDt = rs.getString("unbthgDt");
                String berthN = rs.getString("berthN");
                String status = rs.getString("status");
                Vessel vessel = new Vessel(fullVsIM, abbrVslM, inVoyN, fullInVoyN, outVoyN, bthgDt, unbthgDt, berthN, status);
                queryList.add(vessel);
//                queryMap.put("fullVsIM", fullVsIM);
//                queryMap.put("abbrVslM", abbrVslM);
//                queryMap.put("inVoyN", inVoyN);
//                queryMap.put("fullInVoyN", fullInVoyN);
//                queryMap.put("outVoyN", outVoyN);
//                queryMap.put("btrDt", bthgDt);
//                queryMap.put("unbthgDt", unbthgDt);
//                queryMap.put("berthN", berthN);
//                queryMap.put("status", status);
//                queryList.add(queryMap);
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
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs102", "root", "C289cdf456!")) {
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
}
