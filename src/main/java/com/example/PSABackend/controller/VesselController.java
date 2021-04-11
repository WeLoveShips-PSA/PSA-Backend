package com.example.PSABackend.controller;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.exceptions.DataException;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.VesselService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RequestMapping(path = "vessel")
@RestController
@CrossOrigin
public class VesselController {

    @GetMapping
    @RequestMapping(path = "/getall")
    public ResponseEntity<?> getAllVessels () {
        try {
            List<VesselDetails> vesselList = VesselService.getAllVessels();
            return ResponseEntity.status(HttpStatus.OK).body(vesselList);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/getvessel")
    public ResponseEntity<?> getVesselById (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM");
        String inVoyN = body.get("inVoyN");
        try {
            VesselDetails vessel = VesselService.getVesselById(abbrVslM, inVoyN);
            return ResponseEntity.status(HttpStatus.OK).body(vessel);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/get-vessel-by-shortAbbrVslM")
    public ResponseEntity<?> getVesselByAbbrVslM (@RequestBody Map<String, String> body) {
        String shortAbbrVslM = body.get("abbrVslM");
        String date= body.get("date");
        try {
            List<VesselDetails> vesselList = VesselService.getVesselByAbbrVslM(shortAbbrVslM, date);
            return ResponseEntity.status(HttpStatus.OK).body(vesselList);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/getvesselsbydate")
    public ResponseEntity<?> getVesselsByDate (@RequestBody Map<String, String> body) {
        String date = body.get("date");
        //sort by
        //order
        LocalDateTime dateTime = LocalDateTime.parse(date);
        try {
            List<VesselDetails> vesselList = VesselService.getVesselsByDate(dateTime);
            return ResponseEntity.status(HttpStatus.OK).body(vesselList);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path= "/get-vessel-speed-history")
    public ResponseEntity<?> getVesselSpeedHistory (@RequestBody Map<String, String> body) {
        String vsl_voy = body.get("vsl_voy").toString();
        try {
            List<TreeMap> vesselSpeedHistory =  VesselService.getVesselSpeedHistory(vsl_voy);
            return ResponseEntity.status(HttpStatus.OK).body(vesselSpeedHistory);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/get-vessel-current-details")
    public ResponseEntity<?> getVesselCurrentDetails (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM").toString();
        String inVoyN = body.get("inVoyN").toString();

        try {
            Map<String, String> currentDetails = VesselService.getCurrentVesselDetails(abbrVslM, inVoyN);
            return ResponseEntity.status(HttpStatus.OK).body(currentDetails);
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping(path = "/get-vessel-previous-details")
    public ResponseEntity<?> getVesselPreviousDetails (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM").toString();
        String inVoyN = body.get("inVoyN").toString();

        try {
            Map<String, String> previousDetails = VesselService.getPreviousVesselDetails(abbrVslM, inVoyN);
            if (previousDetails == null) {
                return ResponseEntity.status(HttpStatus.OK).body(null);

            }
            return ResponseEntity.status(HttpStatus.OK).body(previousDetails);
        } catch (PSAException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
