package com.example.PSABackend;

import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.DAO.PortNetConnectorDAO;
import com.example.PSABackend.exceptions.DataException;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.AlertService;
import com.example.PSABackend.service.EmailService;
import com.example.PSABackend.service.VesselService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.example.PSABackend.service.UserService;


@Component
public class PortNetConnector {

    @Value("${portnet.apikey}")
    private String apiKey;
    @Value("${spring.datasource.url}")
    private String dbURL;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    private AlertService alertService = new AlertService();
    private UserService userService;
    private EmailService emailService;



    // Calls the vessel api to get all the berthing time and status of the vessels
    public void getUpdate(String dateFrom, String dateTo) {
        PortNetConnectorDAO portNetConnectorDAO = new PortNetConnectorDAO();
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
            JsonArray vesselArray = (JsonArray) jsonObject.get("results").getAsJsonArray();
            // Inserts the vessel information into the vessel table

            portNetConnectorDAO.insert(vesselArray);

        }
    }





    // Calls the api for the individual vessels and calls PortNetConnectorDAO.insertIndividualVessels
    // to update the individual vessels in the database
    public void updateVessel() throws DataException {
        PortNetConnectorDAO portNetConnectorDAO = new PortNetConnectorDAO();
        String url = "https://api.portnet.com/extapi/vessels/predictedbtr/?vslvoy=";
        String getQuery = "";
        ArrayList<HashMap<String, String>> queryArray = new ArrayList<>();
        HashMap<String, String> queryMap = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Apikey", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity(headers);


        queryArray = portNetConnectorDAO.getAllShipName();
        int i = 0;
        int ind = 1;
        for (HashMap<String, String> v : queryArray) {
            System.out.println("Looking for extra changes " + ind ++);
            // Program waits for 1 second
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(++i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            StringBuilder queryParam = new StringBuilder();
            queryParam.append(url);
            queryParam.append(v.get("vsl_voy"));
            System.out.println(queryParam.toString());
            ResponseEntity<String> response = null;
            try {
                response = restTemplate.exchange(queryParam.toString(), HttpMethod.GET, entity, String.class);
            } catch (RestClientResponseException | ResourceAccessException e) {
                System.out.println("Failed to get remote resource because: " + e.getMessage());
                continue;
            }
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();
            System.out.println(jsonObject.toString());
            try {
                if (jsonObject.get("Error") == null) {
                    portNetConnectorDAO.insertIndividualVessels(jsonObject, v.get("abbrVslM"), v.get("inVoyN"), v.get("vsl_voy"));
                } else {
                    portNetConnectorDAO.setVesselIsUpdated(v.get("abbrVslM"), v.get("inVoyN"), v.get("vsl_voy"), false);
                }
            } catch (DataException e) {
                throw e;
            }
        }
    }


    @Scheduled(cron = "0 0 8,20 * * *")
    public void daily() {
        System.out.println("Daily update: " + LocalDateTime.now());

        try {
            updateVessel();
            alertService.getAlerts();
        } catch (PSAException e) {
            System.out.println(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void nextWeek(){
        System.out.println("Weekly update: " + LocalDateTime.now());

        LocalDate localDate = LocalDate.now();
        String todayDate = localDate.toString();
        String nextWeekDate = localDate.plusDays(7).toString();
        getUpdate(todayDate, nextWeekDate);
        try {
            updateVessel();
            alertService.getAlerts();
        } catch (PSAException e) {
            System.out.println(e.getMessage());
        }
    }

//    @Scheduled(cron = "0 0 1-11,13-23 * * *")
//    public void hourly(){
//        System.out.println("Hourly update: " + LocalDateTime.now());
//        try {
//            updateVessel();
//            alertService.getAlerts();
//        } catch (PSAException e) {
//            System.out.println(e.getMessage());
//        }
//    }


}