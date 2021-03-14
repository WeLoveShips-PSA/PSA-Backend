package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.DAO.VesselExtraDAS;
import com.example.PSABackend.classes.Vessel;
import com.example.PSABackend.classes.VesselDetails;
import com.example.PSABackend.classes.VesselExtra;
import org.springframework.stereotype.Service;
import java.time.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class VesselDetailsService {
    public static ArrayList<VesselDetails> getAllVesselDetails () {
        ArrayList<VesselDetails> allVesselDetails = new ArrayList<>();
        ArrayList<Vessel> vessels = VesselDAS.selectAllVessels();
        String VSLVoy = null;
        String tempFullVsIM = null; //for vslvoy
        String tempInVoyN = null; //for vslvoy

        VesselExtra vesselExtra = null;
        VesselDetails vesselDetails = null;

        for (Vessel vessel : vessels) {
            tempFullVsIM = vessel.getFullVslM().replaceAll("\\s+", "");
            tempInVoyN = vessel.getInVoyN().replaceAll("\\s+", "");
            StringBuilder queryParams = new StringBuilder();
            queryParams.append(tempFullVsIM);
            queryParams.append(tempInVoyN);
            VSLVoy = queryParams.toString(); //create vslvoy using params from 1st api
            vesselExtra = VesselExtraService.getVesselExtraByVSLVoy(VSLVoy); //uses vslvoy to retrieve matching 2nd api details
            String avgSpeed = null;
            String maxSpeed = null;
            String distanceToGo = null;
            if(vesselExtra != null){
                avgSpeed = vesselExtra.getAvgSpeed();
                maxSpeed = vesselExtra.getMaxSpeed();
                distanceToGo = vesselExtra.getDistanceToGo();
            }
            String fullVsIM = vessel.getFullVslM();
            String inVoyN = vessel.getInVoyN();
            String outVoyN = vessel.getOutVoyN();
            String berthTime = vessel.getBthgDt();
            String berthNo = vessel.getBerthNo();
            String status = vessel.getStatus();
            vesselDetails = new VesselDetails(fullVsIM, inVoyN, outVoyN, avgSpeed, maxSpeed, distanceToGo, berthTime, berthNo, status);
            allVesselDetails.add(vesselDetails);
        }
        return allVesselDetails;
    }

    //uses the above method to get all vesseldetails then filters out the vesseldetails with berthtime
    //that falls in between dateFrom and dateTo
    public static ArrayList<VesselDetails> getAllVesselDetailsByDate (String dateFrom, String dateTo) {
        ArrayList<VesselDetails> vesselDetails = getAllVesselDetails();
        ArrayList<VesselDetails> vesselDetailsByDate = new ArrayList<>();

        LocalDateTime localDateTimeFrom = LocalDateTime.parse(dateFrom);
        LocalDateTime localDateTimeTo = LocalDateTime.parse(dateTo);

        for (VesselDetails vesselDetail : vesselDetails) {
            LocalDateTime berthTime = LocalDateTime.parse(vesselDetail.getBerthTime());
            if (berthTime.isAfter(localDateTimeFrom) && berthTime.isBefore(localDateTimeTo)) {
                vesselDetailsByDate.add(vesselDetail); //if berthTime of vessel falls between range of dates given
            }
        }
        return vesselDetailsByDate;
    }

    //retrieves vessel from first api, then creates vslvoy
    public static VesselDetails getVesselDetailsById (String abbrVslM, String inVoyN){
        Vessel vessel = VesselDAS.selectVesselById(abbrVslM, inVoyN);
        String fullVslM = vessel.getFullVslM().replaceAll("\\s+", "");
        inVoyN = inVoyN.replaceAll("\\s+", "");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append(fullVslM);
        queryParams.append(inVoyN);
        String VSLVoy = queryParams.toString(); //create vslvoy using params from 1st api
        VesselExtra vesselExtra = VesselExtraService.getVesselExtraByVSLVoy(VSLVoy);

        String avgSpeed = null;
        String maxSpeed = null;
        String distanceToGo = null;

        if(vesselExtra != null){
            avgSpeed = vesselExtra.getAvgSpeed();
            maxSpeed = vesselExtra.getMaxSpeed();
            distanceToGo = vesselExtra.getDistanceToGo();
        }

        fullVslM = vessel.getFullVslM();
        inVoyN = vessel.getInVoyN();
        String outVoyN = vessel.getOutVoyN();
        String berthTime = vessel.getBthgDt();
        String berthNo = vessel.getBerthNo();
        String status = vessel.getStatus();

        return new VesselDetails(fullVslM,inVoyN,outVoyN,avgSpeed,maxSpeed,distanceToGo,berthTime,berthNo,status);
    }
}
