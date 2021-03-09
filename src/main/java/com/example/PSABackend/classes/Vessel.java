package com.example.PSABackend.classes;

import javax.validation.constraints.NotBlank;

public class Vessel {

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

    private final String bthgDt;
    private final String unbthgDt;
    private final String berthN;
    private final String status;

    public Vessel(@NotBlank String fullVslM, @NotBlank String abbrVslM, @NotBlank String inVoyN, @NotBlank String fullInVoyN, @NotBlank String outVoyN, String bthgDt, String unbthgDt, String berthN, String status) {
        this.fullVslM = fullVslM;
        this.abbrVslM = abbrVslM;
        this.inVoyN = inVoyN;
        this.fullInVoyN = fullInVoyN;
        this.outVoyN = outVoyN;
        this.bthgDt = bthgDt;
        this.unbthgDt = unbthgDt;
        this.berthN = berthN;
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

    public String getBthgDt() {
        return bthgDt;
    }

    public String getUnbthgDt() {
        return unbthgDt;
    }

    public String getBerthN() {
        return berthN;
    }

    public String getStatus() {
        return status;
    }



    // RuiXian 123
}
