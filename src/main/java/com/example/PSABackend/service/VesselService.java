package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VesselService {

    public static ArrayList<VesselDetails> getAllVessels () {
        return VesselDAS.selectAllVessels();
    }

    public static Vessel getVesselById (String abbrVslM, String inVoyN){
        return VesselDAS.selectVesselById(abbrVslM, inVoyN);
    }

    public static List<VesselDetails> getVesselByAbbrVslM(String shortAbbrVslM) {
        return VesselDAS.getVesselByAbbrVslM(shortAbbrVslM);
    }
}
