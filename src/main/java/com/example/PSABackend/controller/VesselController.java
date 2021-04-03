package com.example.PSABackend.controller;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.VesselService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ArrayList<VesselDetails> getAllVessels () {
        try {
            return VesselService.getAllVessels();
        } catch (PSAException e) {
            return null;
        }
    }

    @PostMapping
    @RequestMapping(path = "/getvessel")
    public Vessel getVesselById (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM");
        String inVoyN = body.get("inVoyN");
        try {
            return VesselService.getVesselById(abbrVslM, inVoyN);
        } catch (PSAException e) {
            return null;
        }
    }

    @PostMapping
    @RequestMapping(path = "/get-vessel-by-shortAbbrVslM")
    public List<VesselDetails> getVesselByAbbrVslM (@RequestBody Map<String, String> body) {
        String shortAbbrVslM = body.get("abbrVslM");
        try {
            return VesselService.getVesselByAbbrVslM(shortAbbrVslM);
        } catch (PSAException e) {
            return null;
        }
    }

    @PostMapping
    @RequestMapping(path = "/getvesselsbydate")
    public List<VesselDetails> getVesselsByDate (@RequestBody Map<String, String> body) {
        String date = body.get("date");
        //sort by
        //order
        LocalDateTime dateTime = LocalDateTime.parse(date);
        try {
            return VesselService.getVesselsByDate(dateTime);
        } catch (PSAException e) {
            return null;
        }
    }

    @PostMapping
    @RequestMapping(path= "/get-vessel-speed-history")
    public List<TreeMap> getVesselSpeedHistory (@RequestBody Map<String, String> body) {

        String vsl_voy = body.get("vsl_voy").toString();
        try {
            return VesselService.getVesselSpeedHistory(vsl_voy);
        } catch (PSAException e) {
            return null;
        }
    }
}
