package com.example.PSABackend.DAO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PortNetConnectorDAO {
    private static String dbURL;
    private static String username;
    private static String password;

    @Value("${spring.datasource.url}")
    public void setdbURL(String value) {
        PortNetConnectorDAO.dbURL = value;
    }

    @Value("${spring.datasource.username}")
    public void setdbUser(String value) {
        PortNetConnectorDAO.username = value;
    }

    @Value("${spring.datasource.password}")
    public void setdbPass(String value) {
        PortNetConnectorDAO.password = value;
    }

    public static void insert(JsonArray vesselArray){
        for(JsonElement e: vesselArray){
            JsonObject vesselObject = e.getAsJsonObject();

            try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs123", "root", "Password1")){
                String query = "SELECT * FROM VESSEL WHERE (abbrVsim = ? AND inVoyn = ? AND unbthgDt = ?)";
                PreparedStatement queryStatement = conn.prepareStatement(query);
                System.out.println(vesselObject);
                String abbr = vesselObject.get("abbrVslM").toString();
                abbr = abbr.replace("\"", "");
                String voy = vesselObject.get("inVoyN").toString();
                voy = voy.replace("\"", "");
                String bthg = vesselObject.get("unbthgDt").toString();
                bthg = bthg.replace("\"", "");
                queryStatement.setString(1, abbr);
                queryStatement.setString(2, voy);
                queryStatement.setString(3, bthg);
                System.out.println(queryStatement.toString());

                ResultSet rs = queryStatement.executeQuery();

                if(rs.next()){
                    System.out.println("exist");
                }else{
                    System.out.println("Doesnt exist");
                }

                String replace = "REPLACE INTO VESSEL VALUES(?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement replaceStatement = conn.prepareStatement(replace);

                String[] arr = {"fullVslM", "abbrVslM", "inVoyN", "fullOutVoyN", "outVoyN", "bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
                for(int i = 0; i<arr.length; i++){
                    String str = vesselObject.get(arr[i]).toString();
                    str = str.replace("\"", "");
                    replaceStatement.setString(i+1, str);
                }

//                System.out.println(replaceStatement.toString());
                replaceStatement.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static void insertIndividualVessels(JsonObject vessel, String abbrVslM, String inVoyN){
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs123", "root", "Password1")){
            String replace = "REPLACE INTO VESSEL_EXTRA VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement replaceStatement = conn.prepareStatement(replace);
            String[] params = {"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
            , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"};
            for(int i = 1; i<= params.length; i++){
                replaceStatement.setString(i, vessel.get(params[i-1]).toString());
            }
            replaceStatement.setString(10, abbrVslM);
            replaceStatement.setString(11, inVoyN);
            replaceStatement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }


    public static ArrayList<HashMap<String, String>> getAllShipName(){
        HashMap<String, String> queryMap = new HashMap<>();
        ArrayList<HashMap<String, String>> queryList = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs123", "root", "Password1")){
            String query = "SELECT fullVsiM, invoyN FROM VESSEL";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String fullVsIM = rs.getString("fullVsIM");
                String inVoyN = rs.getString("inVoyN");
                String abbrVslM = rs.getString("abbrVslM");
                fullVsIM = fullVsIM.replaceAll("\\s+", "");
                inVoyN = inVoyN.replaceAll("\\s+", "");
                StringBuilder queryParams = new StringBuilder();
                queryParams.append(fullVsIM);
                queryParams.append(inVoyN);
                System.out.println(queryParams);
                queryMap.put("vsl_voy", queryParams.toString());
                queryMap.put("abbrVslM", abbrVslM);
                queryMap.put("inVoyN", inVoyN);
//                String[] res = {queryParams.toString(), abbrVslM, inVoyN};
                queryList.add(queryMap);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return queryList;
//        return queryList;
    }

}
