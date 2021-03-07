package com.example.PSABackend;

import com.example.PSABackend.DAO.PortNetConnectorDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;
import java.sql.*;

@Component
public class  PortNetConnector {

    private static String apiKey;

    @Value("${portnet.apikey}")
    public void setApiKey(String value) {
        PortNetConnector.apiKey = value;
    }

    // Calls the vessel api to get all the berthing time and status of the vessels
    public static void getUpdate(String dateFrom, String dateTo) {
        String url = "https://api.portnet.com/vsspp/pp/bizfn/berthingSchedule/retrieveByBerthingDate/v1.2";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Apikey", apiKey);

        Map<String, Object> map = new HashMap<>();
        map.put("dateFrom", dateFrom);
        map.put("dateTo", dateTo);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();
            System.out.println(jsonObject.toString());
            JsonArray vesselArray = (JsonArray) jsonObject.get("results").getAsJsonArray();
            // Inserts the vessel information into the vessel table
            PortNetConnectorDAO.insert(vesselArray);
        }
    }

    // Calls the api for the individual vessels and calls PortNetConnectorDAO.insertIndividualVessels
    // to update the individual vessels in the database
    public static void updateVessel(){
        String url = "https://api.portnet.com/extapi/vessels/predictedbtr/?vslvoy=";
        String getQuery = "";
        ArrayList<HashMap<String, String>> queryArray = new ArrayList<>();
        HashMap<String, String> queryMap = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Apikey", apiKey);
        HttpEntity entity= new HttpEntity(headers);

        // PortNetConnectorDAO.getAllShipName() returns an array of hasmaps containing the
        // Jsonobject of the vessel, and the abbrvslm and invoyn of the vessel
        queryArray = PortNetConnectorDAO.getAllShipName();
        for(HashMap<String, String> v: queryArray){
            // The structure of v is such that these are the keys 0: vsl_voy, 1: abbrVslM, 2: inVoyN
            StringBuilder thing = new StringBuilder();
            thing.append(url);
            thing.append(v.get("vsl_voy"));
//            System.out.println(v.get("vsl_voy"));
//            System.out.println(v.get("abbrVslM"));
            ResponseEntity<String> response = restTemplate.exchange(thing.toString(), HttpMethod.GET, entity, String.class);
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();
            System.out.println(response.getBody());
            if(jsonObject.get("Error") == null) {
//                JsonArray vesselArray = (JsonArray) jsonObject.get("results").getAsJsonArray();
//                JsonObject actualVessel = (JsonObject) vesselArray.get(0);
//                 Inserts the individual vessel information into the vessel_extra table
                System.out.println(jsonObject.toString());
                PortNetConnectorDAO.insertIndividualVessels(jsonObject, v.get("abbrVslM"), v.get("inVoyN"));
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public static void daily(){
        LocalDate localDate = LocalDate.now();
        String todaydate = localDate.toString();
        System.out.println(todaydate);
        getUpdate(todaydate, todaydate);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public static void nextWeek(){
        LocalDate localDate = LocalDate.now();
        String todayDate = localDate.toString();
        String nextWeekDate = localDate.plusDays(7).toString();
        getUpdate(todayDate, nextWeekDate);
    }

    @Scheduled(cron = "* * 1 * * *")
    public static void hourly(){

    }
}