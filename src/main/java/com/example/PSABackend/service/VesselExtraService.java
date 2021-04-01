package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselExtraDAS;
import com.example.PSABackend.classes.VesselExtra;
import com.example.PSABackend.exceptions.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VesselExtraService {
    public static ArrayList<VesselExtra> getAllVesselExtra() throws DataException {
        return VesselExtraDAS.selectAllExtraVessels();
    }

    public static VesselExtra getVesselExtraByVSLVoy (String VSLVoy) throws DataException {
        return VesselExtraDAS.selectExtraVesselByVSLVoy(VSLVoy);
    }

}
