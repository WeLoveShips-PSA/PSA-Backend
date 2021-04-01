package com.example.PSABackend.controller;

import com.example.PSABackend.classes.VesselExtra;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.VesselExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RequestMapping(path = "vessel_extra")
@RestController
public class VesselExtraController {
    @GetMapping
    @RequestMapping("/getall")
    public ArrayList<VesselExtra> getAllVesselExtra (){
        try {
            return VesselExtraService.getAllVesselExtra();
        } catch (PSAException e) {
            return null;
        }
    }

    @PostMapping
    @RequestMapping("/getvessel")
    public VesselExtra getVesselExtra (@RequestBody Map<String, String> body){
        String vslVoy = body.get("VSL_Voy");
        try {
            return VesselExtraService.getVesselExtraByVSLVoy(vslVoy);
        } catch (PSAException e) {
            return null;
        }
    }
}
