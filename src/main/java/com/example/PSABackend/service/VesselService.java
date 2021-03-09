package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VesselService {
    private final VesselDAS vesselDAS;

    @Autowired
    public VesselService (@Qualifier("pregres") VesselDAS vesselDAS) {this.vesselDAS = vesselDAS;}

    public ArrayList<JSONObject> getAllVessels () {
        ArrayList<JSONObject> allVessels = new ArrayList<JSONObject>();
        for(int i = 0 ; i < vesselDAS.selectAllVessels().size() ; i++){
            allVessels.add(new JSONObject(vesselDAS.selectAllVessels().get(i)));
        }
        return allVessels;
    }

    public JSONObject getVesselById (String abbrVslM, String inVoyN){
        return new JSONObject(vesselDAS.selectVesselById(abbrVslM, inVoyN));
    }
}
