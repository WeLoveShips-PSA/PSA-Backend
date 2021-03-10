package com.example.PSABackend.controller;

import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.service.VesselService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "vessel")
@RestController
public class VesselController {
    //frontend will send date parameters to controller and need to retrieve vessels arriving between the dates specified
    //frontend will give me vsl_voy, i return object vessel_extra

    @GetMapping
    @RequestMapping(path = "/getall")
    public ArrayList<Vessel> getAllVessels () {
        return VesselService.getAllVessels();
    }

    @PostMapping
    @RequestMapping(path = "/getvessel")
    public Vessel getVesselById (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM");
        String inVoyN = body.get("inVoyN");
        return VesselService.getVesselById(abbrVslM, inVoyN);
    }
}
