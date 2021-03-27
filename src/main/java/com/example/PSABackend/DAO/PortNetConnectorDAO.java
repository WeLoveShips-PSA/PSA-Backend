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

                ResultSet rs = queryStatement.executeQuery();



                if(rs.next()){
                    String[] arr = {"bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
                    String update = "UPDATE INTO VESSEL SET bthgdt = ?, unbthgdt = ?, berthn = ?, status = ?, abbrterminalm = ? WHERE abbrvslm = ? and invoyn = ?";
                    PreparedStatement updateStatement = conn.prepareStatement(update);
                    for(int i = 0; i<arr.length; i++){
                        String str = vesselObject.get(arr[i]).toString();
                        str = str.replace("\"", "");
                        // Set datetime from json to format of mysql
                        if(arr[i] == "bthgDt" || arr[i] == "unbthgDt") {
                            String[] date_time = str.split("T");
                            str = date_time[0] + " " + date_time[1];
                            updateStatement.setString(i + 1, str);
                        }
                        updateStatement.setString(arr.length+1, vesselObject.get("abbrVslM").toString());
                        updateStatement.setString(arr.length+2, vesselObject.get("inVoyN").toString());
                    }
                }else{
                    String insert = "INSERT INTO VESSEL VALUES(?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement insertStatement = conn.prepareStatement(insert);

                    String[] arr = {"fullVslM", "abbrVslM", "inVoyN", "fullOutVoyN", "outVoyN", "bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
                    for(int i = 0; i<arr.length; i++){
                        String str = vesselObject.get(arr[i]).toString();
                        str = str.replace("\"", "");
                        // Set datetime from json to format of mysql
                        if(arr[i] == "bthgDt" || arr[i] == "unbthgDt"){
                            String[] date_time = str.split("T");
                            str = date_time[0] + " " + date_time[1];
                        }
                        insertStatement.setString(i+1, str);
                    }
                    insertStatement.executeUpdate();
                }

//                String replace = "REPLACE INTO VESSEL VALUES(?,?,?,?,?,?,?,?,?,?)";
//                PreparedStatement replaceStatement = conn.prepareStatement(replace);
//
//                //Loops through the param names for the vesselObject
//                String[] arr = {"fullVslM", "abbrVslM", "inVoyN", "fullOutVoyN", "outVoyN", "bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
//                for(int i = 0; i<arr.length; i++){
//                    String str = vesselObject.get(arr[i]).toString();
//                    str = str.replace("\"", "");
//                    // Set datetime from json to format of mysql
//                    if(arr[i] == "bthgDt" || arr[i] == "unbthgDt"){
//                        String[] date_time = str.split("T");
//                        str = date_time[0] + " " + date_time[1];
//                    }
//                    replaceStatement.setString(i+1, str);
//                }
//                replaceStatement.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void insertIndividualVessels(JsonObject vessel, String abbrVslM, String inVoyN, String vsl_voy){
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

            String query = String.format("SELECT avg(AVG_SPEED) speed FROM VESSEL_SPEED WHERE VSL_VOY = '%s'", vsl_voy);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            speed = Double.parseDouble(vessel.get("AVG_SPEED").toString());
            if(rs.next() && rs.getDouble("speed") > 0.0){
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
            // Getting date which is 3 days from now
            LocalDate localDate = LocalDate.now().plusDays(3);

            // Making the SQL query which gets vessel coming 3 days from now
            String query = "SELECT fullVslM, invoyN, abbrVslM FROM VESSEL WHERE BTRDT <= " + "'" + localDate.toString() + "'";
            System.out.println(query);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {

                // Puts fullVsIM, inVoyN, abbrVslM into a map so that updateVessel function in
                // PortnetConnector can use the information to insert vesselExtra information into
                // the database, and formulate vsl_voy to call the second api

                // Not the best way to do this, but it is what it is

                HashMap<String, String> queryMap = new HashMap<>();
                String fullVslM = rs.getString("fullVslM");
                String inVoyN = rs.getString("inVoyN");
                String abbrVslM = rs.getString("abbrVslM");

                // Removing all spaces and slashes from invoyn and abbrvslm
                fullVslM = fullVslM.replaceAll("\\s+", "");
                inVoyN = inVoyN.replaceAll("\\s+|/", "");

                // Formulating vsl_voy for updateVessel method in PortnetConnector
                StringBuilder queryParams = new StringBuilder();
                queryParams.append(fullVslM);
                queryParams.append(inVoyN);
                System.out.println(queryParams);

                // Putting the stuffs into a map
                queryMap.put("vsl_voy", queryParams.toString());
                queryMap.put("abbrVslM", abbrVslM);
                queryMap.put("inVoyN", inVoyN);

                // Adding the map into an array list to eventually send to the function
                queryList.add(queryMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryList;
//        return queryList;
    }
}
