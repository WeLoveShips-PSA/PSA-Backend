package com.example.PSABackend.service;

import com.example.PSABackend.DAO.VesselDAS;
import com.example.PSABackend.classes.*;
import com.example.PSABackend.exceptions.DataException;
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

    public static List<VesselDetails> getVesselByAbbrVslM(String shortAbbrVslM) throws DataException {
        return VesselDAS.getVesselByAbbrVslM(shortAbbrVslM);
    }

    public static List<Alert> detectVesselChanges(User user, List<FavAndSubVessel> subbedVesselList) throws DataException {
        return VesselDAS.detectChangesVessel(user, subbedVesselList);
    }

    public static boolean needAddAlert(ResultSet newRs, ResultSet oldRs, String alertAttribute, boolean alertOpt) throws SQLException {
        if (!alertOpt) {
            return false;
        }
        if (newRs.getString(alertAttribute) == null) {
            return false;
        }
        if (newRs.getString(alertAttribute).equals(oldRs.getString(alertAttribute))) {
            return false;
        }
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
