package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.DataException;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VesselService {

    public static ArrayList<VesselDetails> getAllVessels () throws DataException {
        return VesselDAS.selectAllVessels();
    }

    public static Vessel getVesselById (String abbrVslM, String inVoyN) throws DataException{
        return VesselDAS.selectVesselById(abbrVslM, inVoyN);
    }

    public static List<VesselDetails> getVesselsByDate(LocalDateTime dateTime) throws DataException {
        return VesselDAS.getVesselsByDate(dateTime);
    }

    public static List<VesselDetails> getVesselByAbbrVslM(String shortAbbrVslM) throws DataException {
        return VesselDAS.getVesselByAbbrVslM(shortAbbrVslM);
    }

    public static List<Alert> detectVesselChanges(User user, List<FavAndSubVessel> subbedVesselList) throws DataException {
        List<Alert> alertList = new ArrayList<>();

        for (FavAndSubVessel vessel : subbedVesselList) {
            HashMap<String, String> newRs = VesselDAS.getCurrentVesselDetails(user.getUser_name(), vessel);
            HashMap<String, String> oldRs = VesselDAS.getPreviousVesselDetails(user.getUser_name(), vessel);

            Alert alert = new Alert();
            alert.setAbbrVslM(vessel.getAbbrVslM());
            alert.setInVoyN(vessel.getInVoyN());
            boolean hasChange = false;
            if (newRs != null && newRs.get("is_updated").equals("0")) {
                continue;
            }
            if (needAddAlert(newRs, oldRs, "btrdt", user.isBtrDtAlert())) {
                alert.setNewBerthTime(Timestamp.valueOf(newRs.get("btrdt")).toLocalDateTime());
                hasChange = true;
            }
            if (needAddAlert(newRs, oldRs, "berthn", user.isBerthNAlert())) {
                alert.setNewBerthNo(newRs.get("berthn"));
                hasChange = true;
            }
            if (needAddAlert(newRs, oldRs, "status", user.isStatusAlert())) {
                alert.setNewStatus(newRs.get("status"));
                hasChange = true;
            }
            if (needAddAlert(newRs, oldRs, "avg_speed", user.isAvgSpeedAlert())) {
                alert.setNewAvgSpeed(Double.parseDouble(newRs.get("avg_speed")));
                hasChange = true;
            }
            if (needAddAlert(newRs, oldRs, "distance_to_go", user.isDistanceToGoAlert())) {
                alert.setNewDistanceToGo(Integer.parseInt(newRs.get("distance_to_go")));
                hasChange = true;
            }
            if (needAddAlert(newRs, oldRs, "max_speed", user.isMaxSpeedAlert())) {
                alert.setNewMaxSpeed(Integer.parseInt(newRs.get("max_speed")));
                hasChange = true;
            }
            if (hasChange) {
                alertList.add(alert);
            }
        } return alertList;

    }

    public static boolean needAddAlert(HashMap<String, String> newRs, HashMap<String, String> oldRs, String alertAttribute, boolean alertOpt) {
        if (!alertOpt) {
            return false;
        }
        String newInfo = newRs.get(alertAttribute);
        String oldInfo = null;
        if (oldRs != null) {
            oldInfo = oldRs.get(alertAttribute);
        }

        if (newInfo == null) {
            return false;
        }
        if (oldInfo == null) {
            return true;
        }
        if (newInfo.equals(oldInfo)) {
            return false;
        }
        System.out.println("newRs " + newRs.get(alertAttribute));
        System.out.println("oldRs " + oldRs.get(alertAttribute));
        return true;
    }

    public static List<TreeMap> getVesselSpeedHistory (String vsl_voy) throws DataException {
        return VesselDAS.getVesselSpeedHistory(vsl_voy);
    }

    public static void sortVesselList(ArrayList<Vessel> list, String sort, String order) {
        Comparator<Vessel> compareByDate = Comparator.comparing(Vessel::getBthgDt).thenComparing(Vessel::getFullVslM);
        Comparator<Vessel> compareByName = Comparator.comparing(Vessel::getFullVslM).thenComparing(Vessel::getBthgDt);

        if (sort.equals("date") && order.equals("asc")) {
            Collections.sort(list, compareByDate);
        } else if (sort.equals("date") && order.equals("desc")) {
            Collections.sort(list, compareByDate.reversed());
        } else if (sort.equals("name") && order.equals("asc")) {
            Collections.sort(list, compareByName);
        } else {
            Collections.sort(list, compareByName.reversed());
        }
    }
 }
