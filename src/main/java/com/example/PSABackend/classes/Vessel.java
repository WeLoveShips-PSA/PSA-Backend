package com.example.PSABackend.classes;

public class Vessel {
    private String fullVesselName;
    private String abbrVesselName;
    private String inVoyN;
    private String fullVoyN;
    private String outgoingVoyN;
    private double maxSpeed;
    private double avgSpeed;


    public Vessel(String fullVesselName, String abbrVesselName,
                   String inVoyN, String fullVoyN,
                  String outgoingVoyN, double maxSpeed, double avgSpeed) {
        this.fullVesselName = fullVesselName;
        this.abbrVesselName = abbrVesselName;
        this.inVoyN = inVoyN;
        this.fullVoyN = fullVoyN;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
    }

    public String getVesselName() {
        return fullVesselName;
    }

    public String getVslVoyN() { // for second API call

        return fullVesselName.replaceAll("\\s+", "") + inVoyN;
    }

}
