package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.DAO.VesselExtraDAS;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;

public class VesselExtraService {
    private final VesselExtraDAS vesselExtraDAS;

    @Autowired
    public VesselExtraService (@Qualifier("pregres") VesselExtraDAS vesselExtraDAS) {this.vesselExtraDAS = vesselExtraDAS;}

    public ArrayList<JSONObject> getAllVesselExtra () {
        ArrayList<JSONObject> allVessels = new ArrayList<JSONObject>();
        for(int i = 0 ; i < vesselExtraDAS.selectAllExtraVessels().size() ; i++){
            allVessels.add(new JSONObject(vesselExtraDAS.selectAllExtraVessels().get(i)));
        }
        return allVessels;
    }

    public JSONObject getVesselExtraByVSLVoy (String VSLVoy) {
        return new JSONObject(vesselExtraDAS.selectExtraVesselByVSLVoy(VSLVoy));
    }

}
