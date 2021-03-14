package com.example.PSABackend.controller;

import com.example.PSABackend.service.VesselExtraService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping(path = "vessel_extra")
@RestController
public class VesselExtraController {
    @GetMapping
    @RequestMapping("/getall")
    public ArrayList<JSONObject> getAllVesselExtra (){
        return VesselExtraService.getAllVesselExtra();
    }

    @PostMapping
    @RequestMapping("/get/{vslvoy}")
    public JSONObject getVesselExtra (@PathVariable("vslvoy") String vslVoy){
        return VesselExtraService.getVesselExtraByVSLVoy(vslVoy);
    }
}
