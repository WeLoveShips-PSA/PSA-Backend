dpackage com.example.PSABackend;

import com.example.PSABackend.DAO.PortNetConnectorDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;
import java.sql.*;
import java.util.concurrent.TimeUnit;

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
//    PortNetConnectorDAO portNetConnectorDAO = new PortNetConnectorDAO(dbURL, username, password);

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
    public void updateVessel(){
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
        HttpEntity entity= new HttpEntity(headers);

        // PortNetConnectorDAO.getAllShipName() returns an array of hashmaps containing the
        // Jsonobject of the vessel, and the abbrvslm and invoyn of the vessel
        queryArray = portNetConnectorDAO.getAllShipName();
        int i = 0;
        for(HashMap<String, String> v: queryArray){

            // Program waits for 1 second
            try{
                TimeUnit.SECONDS.sleep(1);
                System.out.println(++i);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            // The structure of v is such that these are the keys 0: vsl_voy, 1: abbrVslM, 2: inVoyN
            StringBuilder queryParam = new StringBuilder();
            queryParam.append(url);
            queryParam.append(v.get("vsl_voy"));
            System.out.println(queryParam.toString());
            ResponseEntity<String> response = restTemplate.exchange(queryParam.toString(), HttpMethod.GET, entity, String.class);
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();

            if(jsonObject.get("Error") == null) {
                portNetConnectorDAO.insertIndividualVessels(jsonObject, v.get("abbrVslM"), v.get("inVoyN"), v.get("vsl_voy"));
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void daily(){
        LocalDate localDate = LocalDate.now();
        String todaydate = localDate.toString();
        System.out.println(todaydate);
        getUpdate(todaydate, todaydate);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void nextWeek(){
        LocalDate localDate = LocalDate.now();
        String todayDate = localDate.toString();
        String nextWeekDate = localDate.plusDays(7).toString();
        getUpdate(todayDate, nextWeekDate);
    }

    @Scheduled(cron = "* * 1 * * *")
    public void hourly(){

    }
}