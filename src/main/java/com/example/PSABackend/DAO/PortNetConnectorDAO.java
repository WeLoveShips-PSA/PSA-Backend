package com.example.PSABackend.DAO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sound.sampled.Port;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@Component
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

//    public PortNetConnectorDAO(String dbURL, String username, String password){
//        this.dbURL = dbURL;
//        this.username = username;
//        this.password = password;
//    }

    public void insert(JsonArray vesselArray){
        for(JsonElement e: vesselArray){
            JsonObject vesselObject = e.getAsJsonObject();

            try(Connection conn = DriverManager.getConnection(dbURL, username, password)){
                String query = "SELECT * FROM VESSEL WHERE (abbrVslM = ? AND inVoyN = ?)";
                PreparedStatement queryStatement = conn.prepareStatement(query);
                System.out.println(vesselObject);
                String abbr = vesselObject.get("abbrVslM").toString();
                abbr = abbr.replace("\"", "");
                String voy = vesselObject.get("inVoyN").toString();
                voy = voy.replace("\"", "");
                queryStatement.setString(1, abbr);
                queryStatement.setString(2, voy);
                System.out.println(queryStatement.toString());

                ResultSet rs = queryStatement.executeQuery();

                if(rs.next()){
                    System.out.println("exist");
                }else{
                    System.out.println("Doesnt exist");
                }

                String replace = "REPLACE INTO VESSEL VALUES(?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement replaceStatement = conn.prepareStatement(replace);

                //Loops through the param names for the vesselObject
                String[] arr = {"fullVslM", "abbrVslM", "inVoyN", "fullOutVoyN", "outVoyN", "bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
                for(int i = 0; i<arr.length; i++){
                    String str = vesselObject.get(arr[i]).toString();
                    str = str.replace("\"", "");
                    // Set datetime from json to format of mysql
                    if(arr[i] == "bthgDt" || arr[i] == "unbthgDt"){
                        String[] date_time = str.split("T");
                        str = date_time[0] + " " + date_time[1];
                    }
                    replaceStatement.setString(i+1, str);
                }
                replaceStatement.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void insertIndividualVessels(JsonObject vessel, String abbrVslM, String inVoyN){
        try(Connection conn = DriverManager.getConnection(dbURL, username, password)){
            String replace = "REPLACE INTO VESSEL_EXTRA VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement replaceStatement = conn.prepareStatement(replace);
            double speed = 0;
            String[] params = {"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
            , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"};
            for(int i = 1; i<= params.length; i++){
                String value = vessel.get(params[i-1]).toString();
                if(value.charAt(0) == '"'){
                    value = value.replace("\"", "");
                }
                replaceStatement.setString(i, value);
            }

            String vsl_voy = vessel.get("VSL_VOY").toString();
            String query = "SELECT avg(AVG_SPEED) speed FROM VESSEL_SPEED WHERE VSL_VOY = " + vsl_voy;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            speed = Double.parseDouble(vessel.get("AVG_SPEED").toString());
            if(rs.next() && rs.getDouble("speed") > 0.0){
                System.out.println(rs.getDouble("speed"));
                if(rs.getDouble("speed") < speed){
                    replaceStatement.setString(10, "1");
                }else{
                    replaceStatement.setString(10, "0");
                }
            }else{
                replaceStatement.setString(10, "0");
            }

            replaceStatement.setString(11, abbrVslM);
            replaceStatement.setString(12, inVoyN);
            replaceStatement.executeUpdate();
            String queryInsert = "INSERT INTO VESSEL_SPEED VALUES(" + vessel.get("AVG_SPEED").toString().replace("\"", "") + ", " + vessel.get("VSL_VOY").toString() + ")";
            System.out.println(queryInsert);
            Statement stmt1 = conn.createStatement();
            stmt1.executeUpdate(queryInsert);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<HashMap<String, String>> getAllShipName(){

        ArrayList<HashMap<String, String>> queryList = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(dbURL, username, password)){
            LocalDate localDate = LocalDate.now().plusDays(3);
            String query = "SELECT fullVsIM, invoyN, abbrVslM FROM VESSEL WHERE BTRDT <= " + "'" + localDate.toString() + "'";
            System.out.println(query);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                HashMap<String, String> queryMap = new HashMap<>();
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
