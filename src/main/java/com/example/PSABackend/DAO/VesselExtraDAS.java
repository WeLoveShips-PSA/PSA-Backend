package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselExtra;
import com.example.PSABackend.exceptions.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//shaun
@Component
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

    public static ArrayList<VesselExtra> selectAllExtraVessels() throws DataException{
        ArrayList<VesselExtra> queryList = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(dbURL, username, password))  {
            String query = "SELECT * FROM VESSEL_EXTRA";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
            //            , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"
            while(rs.next()){
                String avgSpeed = rs.getString("AVG_SPEED");
                String distanceToGo = rs.getString("DISTANCE_TO_GO");
                String isPatchingActivated = rs.getString("IS_PATCHING_ACTIVATED");
                String maxSpeed = rs.getString("MAX_SPEED");
                String patchingPredictedBtr = rs.getString("PATCHING_PREDICTED_BTR");
                String predictedBtr = rs.getString("PREDICTED_BTR");
                String vesselName = rs.getString("VESSEL_NAME");
                String voyageCodeInbound = rs.getString("VOYAGE_CODE_INBOUND");
                String vslVoy = rs.getString("VSL_VOY");

//                queryMap.put("AVG_SPEED", avgSpeed);
//                queryMap.put("DISTANCE_TO_GO", distanceToGo);
//                queryMap.put("IS_PATCHING_ACTIVATED", isPatchingActivated);
//                queryMap.put("MAX_SPEED", maxSpeed);
//                queryMap.put("PATCHING_PREDICTED_BTR", patchingPredictedBtr);
//                queryMap.put("PREDICTED_BTR", predictedBtr);
//                queryMap.put("VESSEL_NAME", vesselName);
//                queryMap.put("VOYAGE_CODE_INBOUND", voyageCodeInbound);
//                queryMap.put("VSL_VOY", vslVoy);
                VesselExtra vesselExtra = new VesselExtra(avgSpeed, distanceToGo, isPatchingActivated, maxSpeed, patchingPredictedBtr, predictedBtr, vesselName, voyageCodeInbound, vslVoy);
                queryList.add(vesselExtra);
//                queryList.add(queryMap);

            }
        } catch (SQLException e){
            throw new DataException("Could not access database");
        }
        return queryList;
    }

    public static VesselExtra selectExtraVesselByVSLVoy (String VSLVoy) throws DataException {
        VesselExtra vesselExtra = null;

        try(Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT * FROM VESSEL_EXTRA WHERE (VSL_VOY = ?)";
            PreparedStatement queryStatement = conn.prepareStatement(query);
            queryStatement.setString(1, VSLVoy);
            System.out.println(queryStatement.toString());

            ResultSet rs = queryStatement.executeQuery();

            //"AVG_SPEED", "DISTANCE_TO_GO", "IS_PATCHING_ACTIVATED", "MAX_SPEED", "PATCHING_PREDICTED_BTR"
            //            , "PREDICTED_BTR", "VESSEL_NAME", "VOYAGE_CODE_INBOUND", "VSL_VOY"

            String avgSpeed = null;
            String distanceToGo = null;
            String isPatchingActivated = null;
            String maxSpeed = null;
            String patchingPredictedBtr = null;
            String predictedBtr = null;
            String vesselName = null;
            String voyageCodeInbound = null;

            if(rs.next()) {
                avgSpeed = rs.getString("AVG_SPEED");
                distanceToGo = rs.getString("DISTANCE_TO_GO");
                isPatchingActivated = rs.getString("IS_PATCHING_ACTIVATED");
                maxSpeed = rs.getString("MAX_SPEED");
                patchingPredictedBtr = rs.getString("PATCHING_PREDICTED_BTR");
                predictedBtr = rs.getString("PREDICTED_BTR");
                vesselName = rs.getString("VESSEL_NAME");
                voyageCodeInbound = rs.getString("VOYAGE_CODE_INBOUND");
            }
            vesselExtra = new VesselExtra(avgSpeed, distanceToGo, isPatchingActivated, maxSpeed, patchingPredictedBtr, predictedBtr, vesselName, voyageCodeInbound, VSLVoy);

        } catch (SQLException e){
            throw new DataException("Could not access database");
        }
        return vesselExtra;
    }
}
