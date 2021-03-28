package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselExtra;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.google.gson.Gson;
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
            String select = String.format("SELECT * FROM VESSEL_EXTRA WHERE VSL_VOY = '%s'",vsl_voy);
            System.out.println(select);
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(select);
            if(rs1.next()){
                String update = "UPDATE VESSEL_EXTRA SET AVG_SPEED = ?, DISTANCE_TO_GO=?,IS_PATCHING_ACTIVATED=?," +
                        "MAX_SPEED=?,PATCHING_PREDICTED_BTR=?,PREDICTED_BTR=?,VESSEL_NAME=?,VOYAGE_CODE_INBOUND=?,VSL_VOY=?,IS_INCREASING=?" +
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

                String query = String.format("SELECT avg(AVG_SPEED) speed FROM VESSEL_SPEED WHERE VSL_VOY = '%s'", vsl_voy);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                speed = Double.parseDouble(vessel.get("AVG_SPEED").toString());
                if(rs.next() && rs.getDouble("speed") > 0.0){
                    if(rs.getDouble("speed") < speed){
                        updateStatement.setString(10, "1");
                    }else{
                        updateStatement.setString(10, "0");
                    }
                }else{
                    updateStatement.setString(10, "0");
                }
                updateStatement.setString(11, abbrVslM);
                updateStatement.setString(12, inVoyN);
                System.out.println(updateStatement.toString());
                updateStatement.executeUpdate();

                String queryInsert = "REPLACE INTO VESSEL_SPEED VALUES(" + vessel.get("AVG_SPEED").toString().replace("\"", "") + ", " + vessel.get("VSL_VOY").toString() + ")";
                System.out.println(queryInsert);
                Statement stmt2 = conn.createStatement();
                stmt2.executeUpdate(queryInsert);

            }else{
                String replace = "INSERT INTO VESSEL_EXTRA VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
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
                replaceStatement.executeUpdate();
                String queryInsert = "INSERT INTO VESSEL_SPEED VALUES(" + vessel.get("AVG_SPEED").toString().replace("\"", "") + ", " + vessel.get("VSL_VOY").toString() + ")";
                System.out.println(queryInsert);
                Statement stmt2 = conn.createStatement();
                stmt2.executeUpdate(queryInsert);
            }



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

    public void lookForChanges(JsonArray vesselArray){


        for(JsonElement element: vesselArray) {
            try(Connection conn=DriverManager.getConnection( "mysql://127.0.0.1:3306/cs102?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false\n")){
                    // check if can loop through the json array
                JsonObject vesselObject= element.getAsJsonObject();
//                Gson gson= new Gson();
//                Vessel vesselObject= gson.fromJson(jsonvesselObject, Vessel.class);

                String object_abbrVslm = vesselObject.get("abbrVslM").toString();
                String object_inVoyn = vesselObject.get("InVoyN").toString();

                String query="Select unbthgDt, btrDt,berthN, status,outVoyN from VESSEL where" +
                        "abbrVslm="+ object_abbrVslm + "inVoyn= " + object_inVoyn;
                Statement stmt= conn.createStatement();
                ResultSet rs= stmt.executeQuery(query);
                String result_set_btrDt=null;
                String result_set_unbthgDt=null;
                String result_set_berthN=null;
                String result_set_status=null;
                String result_set_outVoyN=null;


                while (rs.next()) {// is there another way to do this? given that there will only be one row returned

                    result_set_btrDt=rs.getString("btrDt");
                    result_set_unbthgDt=rs.getString("unbthgDt");
                    result_set_berthN=rs.getString("berthN");
                    result_set_status=rs.getString("status");
                    result_set_outVoyN=rs.getString("outVoyN");

                }

                //create vesselObject

                String object_berthN = vesselObject.get("BerthNo").toString();
                String object_status = vesselObject.get("Status").toString();
                String object_unbthgDt = vesselObject.get("UnbthgDt").toString();
                String object_btrDt = vesselObject.get("BthgDt").toString();
                String  object_outVoyn= vesselObject.get("OutVoyN").toString();//do we need to check change in outVoyn

                if((!(result_set_unbthgDt.equals(object_unbthgDt)))
                        ||(!(result_set_btrDt.equals(object_btrDt)))||
                        (!(result_set_berthN.equals(object_berthN)))||
                        (!(result_set_status.equals(object_status)))||
                        (!(result_set_outVoyN.equals(object_outVoyn)))
                ){
                    Alert alert = new Alert();

                    alert.setVesselName(object_abbrVslm);
                    alert.setInVoyN(object_inVoyn);
                    DateTimeFormatter format= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");// check database

                    if (!(result_set_unbthgDt.equals(object_unbthgDt))) {
                        alert.setNewBerthTime(LocalDateTime.parse(object_unbthgDt,format));
                    }

                    if (!(result_set_btrDt.equals(object_btrDt))) {
                        alert.setNewUnBerthTime(LocalDateTime.parse(object_btrDt,format));
                    }

                    if (!(result_set_berthN.equals(object_berthN))) {
                        alert.setNewBerthNo(object_berthN);
                    }

                    if (!(result_set_status.equals(object_status))) {
                        alert.setNewStatus(object_status);
                    }

                    if (!(result_set_outVoyN.equals(object_outVoyn))) {
                        alert.setOutVoyN(object_outVoyn);
                    }




                    String query_users="Select username from subscribed_VESSEL where"+
                            "abbrVslm="+ object_abbrVslm + "inVoyn= "+ object_inVoyn;;
                    Statement stmt_users= conn.createStatement();
                    ResultSet rs_users= stmt.executeQuery(query);
                    while (rs_users.next()){
                        String result_set_users=rs.getString("username");
                        alert.addUsername(result_set_users);
                    }

                    portNetConnector.getAlertDAO().getList().add(alert);

                }



                //add username query from subscribed vessel, using- return array list of users

            }catch(Exception e){
            //alert list


            }

        }
    }



    //CHANGES WITHIN VESSEL(EXTRA)

    public void lookForExtraChanges(JsonObject vesselObject){
        try(Connection conn=DriverManager.getConnection( "mysql://127.0.0.1:3306/cs102?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false\n")){
//            Gson gson= new Gson();
//            VesselExtra vesselObject= gson.fromJson(jsonObject, VesselExtra.class);


            String object_vsl_voy = vesselObject.get("VslVoy").toString();
            String object_inVoyn = vesselObject.get("VoyageCodeInbound").toString();
            String object_Vesselname= vesselObject.get("VesselName").toString();
            int object_max_speed = Integer.parseInt(vesselObject.get("MaxSpeed()").toString());
            int object_distanceToGo= Integer.parseInt(vesselObject.get("DistanceToGo").toString() );
            double object_avg_speed = Double.parseDouble(vesselObject.get("AvgSpeed").toString());

            String query="Select max_speed,avg_speed,distance_to_go from VESSEL where" +
                    "vsl_voy="+ object_vsl_voy;
            Statement stmt= conn.createStatement();
            ResultSet rs= stmt.executeQuery(query);

            int result_set_max_speed=0;
            double result_set_avg_speed=0.0;
            int result_set_distanceToGo= 0;



            while (rs.next()) {  // is there another way to do this? given that there will only be one row returned
                result_set_max_speed=Integer.parseInt(rs.getString("max_speed"));
                result_set_avg_speed=Double.parseDouble(rs.getString("avg_speed"));
                result_set_distanceToGo= Integer.parseInt(rs.getString("distance_to_go"));
            }
            boolean in_alertList=false;
            for(Alert alert: portNetConnector.getAlertDAO().getList()){

               if(object_Vesselname.equals(alert.getVesselName())) {
                    in_alertList=true;
                   if(alert.getVesselName().equals(object_Vesselname)){
                       if (result_set_avg_speed!= object_avg_speed) {
                           alert.setNewAvgSpeed(object_avg_speed);
                       }

                       if (result_set_distanceToGo!=object_distanceToGo) {
                           alert.setNewDistanceToGo(object_distanceToGo);
                       }

                       if (result_set_max_speed!=object_max_speed) {
                           alert.setNewMaxSpeed(object_max_speed);
                       }
                       break;
                   }

               }
            }

            if(!in_alertList) {
                Alert alert = new Alert();

                alert.setVesselName(object_Vesselname);
                alert.setInVoyN(object_inVoyn);
                if(alert.getVesselName().equals(object_Vesselname)){
                    if (result_set_avg_speed!= object_avg_speed) {
                        alert.setNewAvgSpeed(object_avg_speed);
                    }

                    if (result_set_distanceToGo!=object_distanceToGo) {
                        alert.setNewDistanceToGo(object_distanceToGo);
                    }

                    if (result_set_max_speed!=object_max_speed) {
                        alert.setNewMaxSpeed(object_max_speed);
                    }
                }

            }




        }catch(Exception e){

        }
    }







}









