package com.example.PSABackend.classes;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class VesselDetails {
    @NotBlank
    private final String vesselName;
    @NotBlank
    private final String inVoyN;
    private final String outVoyN;
    private final double avgSpeed; //consider making an avgspeed object
    private final int maxSpeed;
    private final int distanceToGo;
    private final LocalDateTime berthTime;
    private final LocalDateTime unBerthTime;
    private final String berthNo;
    private final String status;
    private final boolean isIncreasing;

    public VesselDetails(@NotBlank String vesselName, @NotBlank String inVoyN, String outVoyN, double avgSpeed, int maxSpeed, int distanceToGo, LocalDateTime berthTime, LocalDateTime unBerthTime, String berthNo, String status, boolean isIncreasing) {
        this.vesselName = vesselName;
        this.inVoyN = inVoyN;
        this.outVoyN = outVoyN;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.distanceToGo = distanceToGo;
        this.berthTime = berthTime;
        this.unBerthTime = unBerthTime;
        this.berthNo = berthNo;
        this.status = status;
        this.isIncreasing = isIncreasing;
    }

    public String getVesselName() {
        return vesselName;
    }

    public String getInVoyN() {
        return inVoyN;
    }

    public String getOutVoyN() {
        return outVoyN;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getDistanceToGo() {
        return distanceToGo;
    }

    public LocalDateTime getBerthTime() {
        return berthTime;
    }

    public LocalDateTime getUnBerthTime() {
        return unBerthTime;
    }

    public String getBerthNo() {
        return berthNo;
    }

    public String getStatus() {
        return status;
    }

    public boolean isIncreasing() {
        return isIncreasing;
    }
}
