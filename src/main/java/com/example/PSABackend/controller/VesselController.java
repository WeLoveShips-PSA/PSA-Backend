package com.example.PSABackend.controller;

import com.example.PSABackend.service.VesselService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping(path = "vessel")
@RestController
public class VesselController {
    //create a controller to send json of all vessels
    //create another controller for all vessels to send json of favorited vessels
    //get all vessels - get request
    //get liked vessels - post request that requires user information

    //frontend will send date parameters to controller and need to retrieve vessels arriving between the dates specified
    //frontend will give me vsl_voy, i return object vessel_extra
    private final VesselService vesselService;

    @Autowired
    public VesselController (VesselService vesselService){
        this.vesselService = vesselService;
    }

    @GetMapping
    @RequestMapping(path = "/getall")
    public ArrayList<JSONObject> getAllVessels () {
        return vesselService.getAllVessels();
    }

    @PostMapping
    @RequestMapping(path = "/get/{abbrVslM}/{inVoyN}")
    public JSONObject getVesselById (@PathVariable("abbrVslM") String abbrVslM, @PathVariable("inVoyN") String inVoyN) {
        return vesselService.getVesselById(abbrVslM, inVoyN);
    }
}
