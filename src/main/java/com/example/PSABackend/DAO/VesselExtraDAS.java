package com.example.PSABackend.DAO;

import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//shaun
public class VesselExtraDAS {
    private static String dbURL;
    private static String username;
    private static String password;

    @Value("${spring.datasource.url}")
    public void setdbURL(String value) {
        VesselExtraDAS.dbURL = value;
    }

    @Value("${spring.datasource.username}")
    public void setdbUser(String value) {
        VesselExtraDAS.username = value;
    }

    @Value("${spring.datasource.password}")
    public void setdbPass(String value) { VesselExtraDAS.password = value; }

    public static ArrayList<HashMap<String, String>> selectAllExtraVessels(){
        ArrayList<HashMap<String, String>> queryList = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs102", "root", "C289cdf456!")) {
            String query = "SELECT * FROM VESSEL_EXTRA";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
            //            , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"
            while(rs.next()){
                HashMap<String, String> queryMap = new HashMap<>();
                String avgSpeed = rs.getString("AVG_SPEED");
                String distanceToGo = rs.getString("DISTANCE_TO_GO");
                String isPatchingActivated = rs.getString("IS_PATCHING_ACTIVATED");
                String maxSpeed = rs.getString("MAX_SPEED");
                String patchingPredictedBtr = rs.getString("PATCHING_PREDICTED_BTR");
                String predictedBtr = rs.getString("PREDICTED_BTR");
                String vesselName = rs.getString("VESSEL_NAME");
                String voyageCodeInbound = rs.getString("VOYAGE_CODE_INBOUND");
                String vslVoy = rs.getString("VSL_VOY");

                queryMap.put("AVG_SPEED", avgSpeed);
                queryMap.put("DISTANCE_TO_GO", distanceToGo);
                queryMap.put("IS_PATCHING_ACTIVATED", isPatchingActivated);
                queryMap.put("MAX_SPEED", maxSpeed);
                queryMap.put("PATCHING_PREDICTED_BTR", patchingPredictedBtr);
                queryMap.put("PREDICTED_BTR", predictedBtr);
                queryMap.put("VESSEL_NAME", vesselName);
                queryMap.put("VOYAGE_CODE_INBOUND", voyageCodeInbound);
                queryMap.put("VSL_VOY", vslVoy);
                queryList.add(queryMap);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return queryList;
    }

    public static HashMap<String, String> selectExtraVesselByVSLVoy (String VSLVoy){
        HashMap<String, String> queryVesselExtra = new HashMap<>();

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs102", "root", "Password1")) {
            String query = "SELECT * FROM VESSEL_EXTRA WHERE (VSL_VOY = ?)";
            PreparedStatement queryStatement = conn.prepareStatement(query);
            queryStatement.setString(1, VSLVoy);
            System.out.println(queryStatement.toString());
            ResultSet rs = queryStatement.executeQuery(query);


            //"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
            //            , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"

            String avgSpeed = rs.getString("AVG_SPEED");
            String distanceToGo = rs.getString("DISTANCE_TO_GO");
            String isPatchingActivated = rs.getString("IS_PATCHING_ACTIVATED");
            String maxSpeed = rs.getString("MAX_SPEED");
            String patchingPredictedBtr = rs.getString("PATCHING_PREDICTED_BTR");
            String predictedBtr = rs.getString("PREDICTED_BTR");
            String vesselName = rs.getString("VESSEL_NAME");
            String voyageCodeInbound = rs.getString("VOYAGE_CODE_INBOUND");

            queryVesselExtra.put("avgSpeed", avgSpeed);
            queryVesselExtra.put("distanceToGo", distanceToGo);
            queryVesselExtra.put("isPatchingActivated", isPatchingActivated);
            queryVesselExtra.put("maxSpeed", maxSpeed);
            queryVesselExtra.put("patchingPredictedBtr", patchingPredictedBtr);
            queryVesselExtra.put("predictedBtr", predictedBtr);
            queryVesselExtra.put("vesselName", vesselName);
            queryVesselExtra.put("voyageCodeInbound", voyageCodeInbound);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return queryVesselExtra;
    }
}
