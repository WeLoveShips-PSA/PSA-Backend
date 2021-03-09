package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselExtraDAS;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class VesselExtraService {
    public static ArrayList<JSONObject> getAllVesselExtra () {
        ArrayList<JSONObject> allVessels = new ArrayList<JSONObject>();
        for(int i = 0 ; i < VesselExtraDAS.selectAllExtraVessels().size() ; i++){
            allVessels.add(new JSONObject(VesselExtraDAS.selectAllExtraVessels().get(i)));
        }
        return allVessels;
    }

    public static JSONObject getVesselExtraByVSLVoy (String VSLVoy) {
        return new JSONObject(VesselExtraDAS.selectExtraVesselByVSLVoy(VSLVoy));
    }

}
