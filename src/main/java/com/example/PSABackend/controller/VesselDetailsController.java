package com.example.PSABackend.controller;

import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.service.VesselDetailsService;
import com.example.PSABackend.service.VesselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "vesseldetails")
@RestController
public class VesselDetailsController {
    //use first api bthDate to do so

    @GetMapping
    @RequestMapping(path = "/getall")
    public ArrayList<VesselDetails> getAllVessels () {
        return VesselDetailsService.getAllVesselDetails();
    }

    @PostMapping
    @RequestMapping(path = "/getallbydate")
    public ArrayList<VesselDetails> getAllVesselsByDate (@RequestBody Map<String, String> body) {
        String dateFrom = body.get("dateFrom");
        String dateTo = body.get("dateTo");
        return VesselDetailsService.getAllVesselDetailsByDate(dateFrom, dateTo);
    }
    //just retrieve regular arraylist, but filter based on date object

    @PostMapping
    @RequestMapping(path = "/getvessel")
    public VesselDetails getVesselById (@RequestBody Map<String, String> body) {
        String abbrVslM = body.get("abbrVslM");
        String inVoyN = body.get("inVoyN");
        return VesselDetailsService.getVesselDetailsById(abbrVslM, inVoyN);
    }
}
