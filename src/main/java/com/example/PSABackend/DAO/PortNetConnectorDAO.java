package com.example.PSABackend.DAO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.sql.*;
import java.util.ArrayList;

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
                abbr = abbr.substring(1,abbr.length()-1);
                String voy = vesselObject.get("inVoyN").toString();
                voy = voy.substring(1,voy.length()-1);
                String bthg = vesselObject.get("unbthgDt").toString();
                bthg = bthg.substring(1,bthg.length()-1);
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
                    str = str.substring(1,str.length()-1);
                    replaceStatement.setString(i+1, str);
                }

//                System.out.println(replaceStatement.toString());
                replaceStatement.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static ArrayList<String> getAllShipName(){
        ArrayList<String> queryList = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cs123", "root", "Password1")){
            String query = "SELECT fullVsim, invoyn FROM VESSEL";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String fullVsim = rs.getString("fullVsim");
                String invoyn = rs.getString("invoyn");
                fullVsim = fullVsim.replaceAll("\\s+", "");
                invoyn = invoyn.replaceAll("\\s+", "");
                StringBuilder queryParams = new StringBuilder();
                queryParams.append(fullVsim);
                queryParams.append(invoyn);
                System.out.println(queryParams);
                queryList.add(queryParams.toString());
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return queryList;
    }

}
