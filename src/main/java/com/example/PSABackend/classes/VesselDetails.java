package com.example.PSABackend.classes;

import javax.validation.constraints.NotBlank;

public class VesselDetails {
    @NotBlank
    private final String vesselName;
    @NotBlank
    private final String incVoyNo;
    private final String outVoyNo;
    private final String avgSpeed; //consider making an avgspeed object
    private final String maxSpeed;
    private final String distanceToGo;
    private final String berthTime;
    private final String berthNo;
    private final String status;
//    private final boolean isIncreasing;

    public VesselDetails(@NotBlank String vesselName, @NotBlank String incVoyNo, String outVoyNo, String avgSpeed, String maxSpeed, String distanceToGo, String berthTime, String berthNo, String status) {
        this.vesselName = vesselName;
        this.incVoyNo = incVoyNo;
        this.outVoyNo = outVoyNo;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.distanceToGo = distanceToGo;
        this.berthTime = berthTime;
        this.berthNo = berthNo;
        this.status = status;
//        this.isIncreasing = isIncreasing;
    }

    public String getVesselName() {
        return vesselName;
    }

    public String getIncVoyNo() {
        return incVoyNo;
    }

    public String getOutVoyNo() {
        return outVoyNo;
    }

    public String getAvgSpeed() { return avgSpeed; }

    public String getMaxSpeed() {
        return maxSpeed;
    }
//
//    public VesselSpeed getVesselSpeed() {
//        return vesselSpeed;
//    }

    public String getDistanceToGo() {
        return distanceToGo;
    }

    public String getBerthTime() {
        return berthTime;
    }

    public String getBerthNo() {
        return berthNo;
    }

    public String getStatus() {
        return status;
    }
}
