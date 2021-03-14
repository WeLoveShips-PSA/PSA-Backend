package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Vessel;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VesselService {

    public static ArrayList<Vessel> getAllVessels () {
        return VesselDAS.selectAllVessels();
    }

    public static Vessel getVesselById (String abbrVslM, String inVoyN){
        return VesselDAS.selectVesselById(abbrVslM, inVoyN);
    }
}
