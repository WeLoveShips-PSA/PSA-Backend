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
            PortNetConnectorDAO.insert(vesselArray);
        }
    }

    public static void updateVessel(){
        String url = "https://api.portnet.com/extapi/vessels/predictedbtr/?vslvoy=";
        String getQuery = "";
        ArrayList<String> queryArray = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Apikey", apiKey);
        HttpEntity entity= new HttpEntity(headers);
        queryArray = PortNetConnectorDAO.getAllShipName();
        for(String v: queryArray){
            StringBuilder thing = new StringBuilder();
            thing.append(url);
            thing.append(v);
            ResponseEntity<String> response = restTemplate.exchange(thing.toString(), HttpMethod.GET, entity, String.class);
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();
            JsonArray vesselArray = (JsonArray) jsonObject.get("results").getAsJsonArray();
            if(vesselArray != null) {
                JsonObject actualVessel = (JsonObject) vesselArray.get(0);
                PortNetConnectorDAO.insertIndividualVessels(actualVessel);
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