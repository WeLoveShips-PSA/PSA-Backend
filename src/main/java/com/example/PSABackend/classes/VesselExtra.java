package com.example.PSABackend.classes;

import javax.validation.constraints.NotBlank;

public class VesselExtra {
    @NotBlank
    private final String vslVoy;
    private final String avgSpeed;
    private final String distanceToGo;
    private final String isPatchingActivated;
    private final String maxSpeed;
    private final String patchingPredictedBtr;
    private final String predictedBtr;
    private final String vesselName;
    private final String voyageCodeInbound;

    public VesselExtra(String avgSpeed, String distanceToGo, String isPatchingActivated, String maxSpeed, String patchingPredictedBtr, String predictedBtr, String vesselName, String voyageCodeInbound, @NotBlank String vslVoy) {
        this.avgSpeed = avgSpeed;
        this.distanceToGo = distanceToGo;
        this.isPatchingActivated = isPatchingActivated;
        this.maxSpeed = maxSpeed;
        this.patchingPredictedBtr = patchingPredictedBtr;
        this.predictedBtr = predictedBtr;
        this.vesselName = vesselName;
        this.voyageCodeInbound = voyageCodeInbound;
        this.vslVoy = vslVoy;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public String getDistanceToGo() {
        return distanceToGo;
    }

    public String getIsPatchingActivated() {
        return isPatchingActivated;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public String getPatchingPredictedBtr() {
        return patchingPredictedBtr;
    }

    public String getPredictedBtr() {
        return predictedBtr;
    }

    public String getVesselName() {
        return vesselName;
    }

    public String getVoyageCodeInbound() {
        return voyageCodeInbound;
    }

    public String getVslVoy() {
        return vslVoy;
    }
}
