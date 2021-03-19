package com.example.PSABackend.classes;

public class FavAndSubVessel {
    private String abbrVslM;
    private String inVoyN;

    public FavAndSubVessel(String abbrVslM, String inVoyN) {
        this.abbrVslM = abbrVslM;
        this.inVoyN = inVoyN;
    }

    public String getAbbrVslM() {
        return abbrVslM;
    }

    public void setAbbrVslM(String abbrVsim) {
        this.abbrVslM = abbrVsim;
    }

    public String getInVoyN() {
        return inVoyN;
    }

    public void setInVoyN(String iinVoyN) {
        this.inVoyN = inVoyN;
    }
}