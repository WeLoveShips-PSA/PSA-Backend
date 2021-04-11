package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselExtra;
import com.example.PSABackend.exceptions.DataException;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.catalina.util.ToStringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sound.sampled.Port;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.classes.Alert;
import com.example.PSABackend.PortNetConnector;

@Component
public class PortNetConnectorDAO {
    private static String dbURL;
    private static String username;
    private static String password;
    private PortNetConnector portNetConnector;

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
        int ind = 1;
        for(JsonElement e: vesselArray){
            JsonObject vesselObject = e.getAsJsonObject();

            if(Integer.parseInt(vesselObject.get("shiftSeqN").toString().replace("\"", "")) == 2){
                continue;
            }

            String query = "SELECT * FROM VESSEL WHERE (abbrVslM = ? AND inVoyN = ?)";

            try(Connection conn = DriverManager.getConnection(dbURL, username, password);
                PreparedStatement queryStatement = conn.prepareStatement(query);){

                String abbr = vesselObject.get("abbrVslM").toString();
                abbr = abbr.replace("\"", "");
                String voy = vesselObject.get("inVoyN").toString();
                voy = voy.replace("\"", "");
                queryStatement.setString(1, abbr);
                queryStatement.setString(2, voy);

                ResultSet rs = queryStatement.executeQuery();



                if(rs.next()){
                    String[] arr = {"bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
                    String update = "UPDATE VESSEL SET btrdt = ?, unbthgdt = ?, berthn = ?, status = ?, abbrterminalm = ?, is_updated = 0 WHERE abbrvslm = ? and invoyn = ?";
                    PreparedStatement updateStatement = conn.prepareStatement(update);
                    for(int i = 0; i<arr.length; i++){
                        String str = vesselObject.get(arr[i]).toString();
                        str = str.replace("\"", "");
                        // Set datetime from json to format of mysql
                        if(arr[i] == "bthgDt" || arr[i] == "unbthgDt") {
                            String[] date_time = str.split("T");
                            str = date_time[0] + " " + date_time[1];
                        }
                        updateStatement.setString(i + 1, str);
                    }
                    updateStatement.setString(arr.length+1, vesselObject.get("abbrVslM").toString().replace("\"", ""));
                    updateStatement.setString(arr.length+2, vesselObject.get("inVoyN").toString().replace("\"", ""));
                    System.out.println(updateStatement.toString());
                    updateStatement.executeUpdate();
                }else{
                    String insert = "INSERT INTO VESSEL VALUES(?,?,?,?,?,?,?,?,?,?,0)";
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
                    System.out.println(insertStatement.toString());
                    insertStatement.executeUpdate();
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void setVesselIsUpdated(String abbrVslM, String inVoyN, String vsl_voy, boolean isUpdated) throws DataException {
        String isVesselUpdatedQuery = "UPDATE VESSEL SET is_updated = ? where abbrvslm = ? and invoyn = ?";
        String isVesselExtraUpdatedQuery = "UPDATE VESSEL_EXTRA SET is_updated = ? where abbrvslm = ? and invoyn = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            PreparedStatement vesselStmt = conn.prepareStatement(isVesselUpdatedQuery);
            PreparedStatement vesselExtraStmt = conn.prepareStatement(isVesselExtraUpdatedQuery);

            vesselStmt.setString(1, isUpdated == true ? "0" : "1");
            vesselStmt.setString(2, abbrVslM);
            vesselStmt.setString(3, inVoyN);

            vesselExtraStmt.setString(1, isUpdated == true ? "0" : "1");
            vesselExtraStmt.setString(2, abbrVslM);
            vesselExtraStmt.setString(3, inVoyN);

            vesselStmt.executeUpdate();
            vesselExtraStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e.getMessage());
        }
    }


    public void insertIndividualVessels(JsonObject vessel, String abbrVslM, String inVoyN, String vsl_voy) throws DataException {
        try(Connection conn = DriverManager.getConnection(dbURL, username, password)){
            String selectQuery = "SELECT * FROM VESSEL_EXTRA WHERE VSL_VOY = ?";
            PreparedStatement stmt = conn.prepareStatement(selectQuery);

            stmt.setString(1, vsl_voy);
            ResultSet rs1 = stmt.executeQuery();
            if(rs1.next()){
                String update = "UPDATE VESSEL_EXTRA SET AVG_SPEED = ?, DISTANCE_TO_GO=?,IS_PATCHING_ACTIVATED=?," +
                        "MAX_SPEED=?,PATCHING_PREDICTED_BTR=?,PREDICTED_BTR=?,VESSEL_NAME=?,VOYAGE_CODE_INBOUND=?,IS_INCREASING=?, IS_UPDATED = 0 " +
                        "WHERE ABBRVSLM = ? AND INVOYN = ?";
                PreparedStatement updateStatement = conn.prepareStatement(update);
                double speed = 0;
                String[] params = {"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
                        , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"};
                for(int i = 1; i<= params.length; i++){
                    String value = vessel.get(params[i-1]).toString();
                    if(value.charAt(0) == '"'){
                        value = value.replace("\"", "");
                    }
                    updateStatement.setString(i, value);
                }
                String getAvgSpeedQuery= "select avg_speed from " +
                        "vessel_extra " +
                        "where vsl_voy = ?";
                // String query = "SELECT avg(AVG_SPEED) speed FROM VESSEL_SPEED WHERE VSL_VOY = ?";
                PreparedStatement stmt2 = conn.prepareStatement(getAvgSpeedQuery);
                stmt2.setString(1, vsl_voy);
                ResultSet rs = stmt2.executeQuery();
                Double currAvgSpeed = Double.parseDouble(vessel.get("AVG_SPEED").toString());
                if(rs.next() && rs.getDouble("avg_speed") > 0.0){
                    if(rs.getDouble("avg_speed") < currAvgSpeed){
                        updateStatement.setString(9, "1");
                    }else{
                        updateStatement.setString(9, "0");
                    }
                }else{
                    updateStatement.setString(9, "0");
                }
                updateStatement.setString(10, abbrVslM);
                updateStatement.setString(11, inVoyN);
                try {
                    updateStatement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    System.out.println(updateStatement);
                }

                // String queryInsert = "REPLACE INTO VESSEL_SPEED VALUES(" + vessel.get("AVG_SPEED").toString().replace("\"", "") + ", " + vessel.get("VSL_VOY").toString() + ")";
                // String queryInsert = "INSERT INTO VESSEL_SPEED VALUES(?, ?)";
//                PreparedStatement stmt3 = conn.prepareStatement(queryInsert);
//                String vessel_avg_speed = vessel.get("AVG_SPEED").toString().replace("\"", "");
//                String vessel_vsl_voy = vessel.get("VSL_VOY").toString().replace("\"", "");
//
//                stmt3.setString(1, vessel_avg_speed);
//                stmt3.setString(2, vessel_vsl_voy);
//
//                stmt3.executeUpdate();

            } else {
                String replace = "INSERT INTO VESSEL_EXTRA VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement replaceStatement = conn.prepareStatement(replace);
                double speed = 0;
                String[] params = {"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
                        , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"};
                for(int i = 1; i<= params.length; i++){
                    String value = vessel.get(params[i-1]).toString();
                    value = value.replace("\"", "");
                    replaceStatement.setString(i, value);
                }
                    replaceStatement.setString(10, "0");
//                String query = String.format("SELECT avg(AVG_SPEED) speed FROM VESSEL_SPEED WHERE VSL_VOY = '%s'", vsl_voy);
//                Statement stmt = conn.createStatement();
//                ResultSet rs = stmt.executeQuery(query);
//                speed = Double.parseDouble(vessel.get("AVG_SPEED").toString());
//                if(rs.next() && rs.getDouble("speed") > 0.0){
//                    if(rs.getDouble("speed") < speed){
//                        replaceStatement.setString(10, "1");
//                    }else{
//                        replaceStatement.setString(10, "0");
//                    }
//                }else{
//                    replaceStatement.setString(10, "0");
//                }

                replaceStatement.setString(11, abbrVslM);
                replaceStatement.setString(12, inVoyN);
                replaceStatement.setString(13, "0");
                System.out.println(replaceStatement.toString());
                replaceStatement.executeUpdate();
//                String queryInsert = "INSERT INTO VESSEL_SPEED VALUES(" + vessel.get("AVG_SPEED").toString().replace("\"", "") + ", " + vessel.get("VSL_VOY").toString() + ")";

//                String queryInsert = "INSERT INTO VESSEL_SPEED VALUES(?, ?)";
//                PreparedStatement stmt4 = conn.prepareStatement(queryInsert);
//                String vessel_avg_speed2 = vessel.get("AVG_SPEED").toString().replace("\"", "");
//                String vessel_vsl_voy2 = vessel.get("VSL_VOY").toString().replace("\"", "");
//
//                stmt4.setString(1, vessel_avg_speed2);
//                stmt4.setString(2, vessel_vsl_voy2);
//
//                stmt4.executeUpdate();
//                System.out.println(stmt4);
            }



        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ArrayList<HashMap<String, String>> getAllShipName(){

        ArrayList<HashMap<String, String>> queryList = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(dbURL, username, password)){
            // Getting date which is 3 days from now
            LocalDate localDate = LocalDate.now().plusDays(3);

            // Making the SQL query which gets vessel coming 3 days from now
            String query = "SELECT fullVslM, invoyN, abbrVslM FROM VESSEL WHERE BTRDT <= " + "'" + localDate.toString() + "'";
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

//    public void lookForChanges(JsonArray vesselArray, AlertDAO alertDAO){
//        int ind = 1;
//        for(JsonElement element: vesselArray) {
//            try(Connection conn=DriverManager.getConnection(dbURL, username, password)){
//                    // check if can loop through the json array
//                JsonObject vesselObject= element.getAsJsonObject();
////                Gson gson= new Gson();
////                Vessel vesselObject= gson.fromJson(jsonvesselObject, Vessel.class);
//
//                String object_abbrVslm = vesselObject.get("abbrVslM").getAsString();
//                String object_inVoyn = vesselObject.get("inVoyN").getAsString();
//                // String query="Select unbthgDt, btrDt,berthN, status,outVoyN from VESSEL where" + "abbrVslm="+ object_abbrVslm + "inVoyn= " + object_inVoyn;
//                String query = "SELECT unbthgDt, btrDt, berthN, status, outVoyN from VESSEL where abbrVslM = ? and inVoyN = ?";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                stmt.setString(1, object_abbrVslm);
//                stmt.setString(2, object_inVoyn);
//                ResultSet rs= stmt.executeQuery();
//                LocalDateTime result_set_btrDt=null;
//                LocalDateTime result_set_unbthgDt=null;
//                String result_set_berthN=null;
//                String result_set_status=null;
//                String result_set_outVoyN=null;
//
//                while (rs.next()) {// is there another way to do this? given that there will only be one row returned
//
//                    result_set_btrDt= rs.getTimestamp("btrDt").toLocalDateTime();
//                    result_set_unbthgDt=rs.getTimestamp("unbthgDt").toLocalDateTime();
//                    result_set_berthN=rs.getString("berthN");
//                    result_set_status=rs.getString("status");
//                    result_set_outVoyN=rs.getString("outVoyN");
//
//                }
//
//                //create vesselObject
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//
//                String object_berthN = vesselObject.get("berthN").toString();
//                String object_status = vesselObject.get("status").toString();
//                String object_unbthgDt_string = vesselObject.get("unbthgDt").getAsString();
//                LocalDateTime object_unbthgDt = LocalDateTime.parse(object_unbthgDt_string, formatter);
//                String object_btrDt_string = vesselObject.get("bthgDt").getAsString();
//                LocalDateTime object_btrDt = LocalDateTime.parse(object_btrDt_string, formatter);
//
//
//
//                String  object_outVoyn= vesselObject.get("outVoyN").toString();//do we need to check change in outVoyn
//
//                if((!(result_set_unbthgDt.equals(object_unbthgDt)))
//                        ||(!(result_set_btrDt.equals(object_btrDt)))||
//                        (!(result_set_berthN.equals(object_berthN)))||
//                        (!(result_set_status.equals(object_status)))||
//                        (!(result_set_outVoyN.equals(object_outVoyn)))
//                ){
//                    Alert alert = new Alert();
//
//                    alert.setAbbrVslM(object_abbrVslm);
//                    alert.setInVoyN(object_inVoyn);
//                    // DateTimeFormatter format= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");// check database
//                    //Timestamp.valueOf(object_btrDt).toLocalDateTime();
//
//                    if (!(result_set_unbthgDt.equals(object_unbthgDt))) {
//                        alert.setNewBerthTime(object_unbthgDt);
//                    }
//
//                    if (!(result_set_btrDt.equals(object_btrDt))) {
//                        alert.setNewBerthTime(object_btrDt);
//                    }
//
//                    if (!(result_set_berthN.equals(object_berthN))) {
//                        alert.setNewBerthNo(object_berthN);
//                    }
//
//                    if (!(result_set_status.equals(object_status))) {
//                        alert.setNewStatus(object_status);
//                    }
//
//                    if (!(result_set_outVoyN.equals(object_outVoyn))) {
//                        alert.setOutVoyN(object_outVoyn);
//                    }
//
//
//
//
////                    String query_users="Select username from subscribed_VESSEL where"+
////                            "abbrVslm="+ object_abbrVslm + "inVoyn= "+ object_inVoyn;
//                    String query_users = "SELECT username FROM subscribed_vessel where abbrVslM = ? and inVoyN = ?";
//                    PreparedStatement stmt2 = conn.prepareStatement(query_users);
//
//                    stmt2.setString(1, object_abbrVslm);
//                    stmt2.setString(2, object_inVoyn);
//
//                    ResultSet rs_users= stmt2.executeQuery();
//                    while (rs_users.next()){
//                        String result_set_users=rs.getString("username");
//                        alert.addUsername(result_set_users);
//                    }
//
//                    alertDAO.getList().add(alert);
//
//                }
//
//
//
//                //add username query from subscribed vessel, using- return array list of users
//
//            }catch(Exception e){
//            //alert list
//                System.out.println(e.getMessage());
//            }
//
//        }
//    }



    //CHANGES WITHIN VESSEL(EXTRA)

//    public void lookForExtraChanges(JsonObject vesselObject, AlertDAO alertDAO){
//        try(Connection conn = DriverManager.getConnection(dbURL,  username, password);){
////            Gson gson= new Gson();
////            VesselExtra vesselObject= gson.fromJson(jsonObject, VesselExtra.class);
//            String object_vsl_voy = vesselObject.get("VSL_VOY").toString();
//            String object_inVoyn = vesselObject.get("VOYAGE_CODE_INBOUND").toString();
//            String object_Vesselname= vesselObject.get("VESSEL_NAME").toString();
//            int object_max_speed = vesselObject.get("MAX_SPEED").getAsInt();
//            int object_distanceToGo= vesselObject.get("DISTANCE_TO_GO").getAsInt();
//            double object_avg_speed = vesselObject.get("AVG_SPEED").getAsDouble();
//
//            // String query="Select max_speed,avg_speed,distance_to_go from VESSEL where" + "vsl_voy="+ object_vsl_voy;
//            String query = "SELECT max_speed, avg_speed, distance_to_go FROM vessel_extra WHERE vsl_voy = ?";
//            PreparedStatement stmt = conn.prepareStatement(query);
//            stmt.setString(1, object_vsl_voy);
//            ResultSet rs= stmt.executeQuery();
//
//
//            int result_set_max_speed=0;
//            double result_set_avg_speed=0.0;
//            int result_set_distanceToGo= 0;
//
//            boolean hasPrevious;
//            while (rs.next()) {  // is there another way to do this? given that there will only be one row returned
//                result_set_max_speed=Integer.parseInt(rs.getString("max_speed"));
//                result_set_avg_speed=Double.parseDouble(rs.getString("avg_speed"));
//                result_set_distanceToGo= Integer.parseInt(rs.getString("distance_to_go"));
//            }
//
//            boolean in_alertList=false;
//            for(Alert alert: alertDAO.getList()){
//
//               if(object_Vesselname.equals(alert.getVesselName())) {
//                    in_alertList=true;
//                   if(alert.getVesselName().equals(object_Vesselname)){
//                       if (result_set_avg_speed!= object_avg_speed) {
//                           alert.setNewAvgSpeed(object_avg_speed);
//                       }
//
//                       if (result_set_distanceToGo!=object_distanceToGo) {
//                           alert.setNewDistanceToGo(object_distanceToGo);
//                       }
//
//                       if (result_set_max_speed!=object_max_speed) {
//                           alert.setNewMaxSpeed(object_max_speed);
//                       }
//                       break;
//                   }
//
//               }
//
//            }
//
//            if(!in_alertList) {
//                Alert alert = new Alert();
//                boolean hasChange = false;
//                alert.setVesselName(object_Vesselname);
//                alert.setInVoyN(object_inVoyn);
//                if (result_set_avg_speed!= object_avg_speed) {
//                    alert.setNewAvgSpeed(object_avg_speed);
//                    hasChange = true;
//                }
//
//                if (result_set_distanceToGo!=object_distanceToGo) {
//                    alert.setNewDistanceToGo(object_distanceToGo);
//                    hasChange = true;
//                }
//
//                if (result_set_max_speed!=object_max_speed) {
//                    alert.setNewMaxSpeed(object_max_speed);
//                    hasChange = true;
//                }
//                if (hasChange == true) {
//                    alertDAO.getList().add(alert);
//                }
//
//            }
//
//
//
//
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//    }







}









