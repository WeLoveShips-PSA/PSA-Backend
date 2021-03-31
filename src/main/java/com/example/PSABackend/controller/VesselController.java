package com.example.PSABackend.controller;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.service.VesselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "vessel")
@RestController
@CrossOrigin
public class VesselController {

    @GetMapping
    @RequestMapping(path = "/getall")
    public ArrayList<VesselDetails> getAllVessels () {
        return VesselService.getAllVessels();
    }

    @PostMapping
    @RequestMapping(path = "/getvessel")
    public Vessel getVesselById (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM");
        String inVoyN = body.get("inVoyN");
        return VesselService.getVesselById(abbrVslM, inVoyN);
    }

    @PostMapping
    @RequestMapping(path = "/get-vessel-by-shortAbbrVslM")
    public List<VesselDetails> getVesselByAbbrVslM (@RequestBody Map<String, String> body) {
        String shortAbbrVslM = body.get("abbrVslM");
        return VesselService.getVesselByAbbrVslM(shortAbbrVslM);
    }

    @PostMapping
    @RequestMapping(path = "/getvesselsbydate")
    public ArrayList<VesselDetails> getVesselsByDate (@RequestBody Map<String, String> body) {
        String date = body.get("date");
        //sort by
        //order
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return VesselDAS.getVesselsByDate(dateTime);
    }
}
