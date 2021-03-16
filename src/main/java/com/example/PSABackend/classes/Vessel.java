package com.example.PSABackend.classes;

import org.apache.tomcat.jni.Local;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Vessel implements Comparable<Vessel>{


    // {"fullVslM", "abbrVslM", "inVoyN", "fullOutVoyN", "outVoyN", "bthgDt", "unbthgDt", "berthN", "status", "abbrTerminalM"};
    @NotBlank
    private final String fullVslM;
    @NotBlank
    private final String abbrVslM;
    @NotBlank
    private final String inVoyN;
    @NotBlank
    private final String fullInVoyN;
    @NotBlank
    private final String outVoyN;

    private final LocalDateTime bthgDt;
    private final LocalDateTime unbthgDt;
    private final String berthNo;
    private final String status;



    public Vessel(@NotBlank String fullVslM, @NotBlank String abbrVslM, @NotBlank String inVoyN, @NotBlank String fullInVoyN, @NotBlank String outVoyN, String bthgDt, String unbthgDt, String berthNo, String status) {
        SimpleDateFormat formatter=new SimpleDateFormat("YYYY-MM-DD''HH:mm:ss");
        this.fullVslM = fullVslM;
        this.abbrVslM = abbrVslM;
        this.inVoyN = inVoyN;
        this.fullInVoyN = fullInVoyN;
        this.outVoyN = outVoyN;
        this.bthgDt = Timestamp.valueOf(bthgDt).toLocalDateTime();
        this.unbthgDt = Timestamp.valueOf(unbthgDt).toLocalDateTime();
        this.berthNo = berthNo;
        this.status = status;
    }

    public String getFullVslM() {
        return fullVslM;
    }

    public String getAbbrVslM() {
        return abbrVslM;
    }

    public String getInVoyN(){return inVoyN;};

    public String getFullOutVoyN() {
        return fullInVoyN;
    }

    public String getOutVoyN(){return outVoyN;}

    public LocalDateTime getBthgDt() {
        return bthgDt;
    }

    public LocalDateTime getUnbthgDt() {
        return unbthgDt;
    }

    public String getBerthNo() { return berthNo; }

    public String getStatus() {
        return status;
    }

    @Override
    public int compareTo(Vessel o) {
        return this.bthgDt.compareTo(o.getBthgDt());
    }

    @Override
    public String toString() {
        return "Vessel{" +
                "fullVslM='" + fullVslM + '\'' +
                ", abbrVslM='" + abbrVslM + '\'' +
                ", inVoyN='" + inVoyN + '\'' +
                ", fullInVoyN='" + fullInVoyN + '\'' +
                ", outVoyN='" + outVoyN + '\'' +
                ", bthgDt=" + bthgDt +
                ", unbthgDt=" + unbthgDt +
                ", berthNo='" + berthNo + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
