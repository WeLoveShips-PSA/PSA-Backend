package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.exceptions.DataException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VesselService {

    public static ArrayList<VesselDetails> getAllVessels () throws DataException {
        return VesselDAS.selectAllVessels();
    }

    public static Vessel getVesselById (String abbrVslM, String inVoyN) throws DataException{
        return VesselDAS.selectVesselById(abbrVslM, inVoyN);
    }

    public static List<VesselDetails> getVesselByAbbrVslM(String shortAbbrVslM) throws DataException {
        return VesselDAS.getVesselByAbbrVslM(shortAbbrVslM);
    }
}
