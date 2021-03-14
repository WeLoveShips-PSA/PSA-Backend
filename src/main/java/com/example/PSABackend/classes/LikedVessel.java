package com.example.PSABackend.classes;

public class LikedVessel {
    private String abbrVsim;
    private String inVoyn;

    public LikedVessel(String abbrVsim, String inVoyn) {
        this.abbrVsim = abbrVsim;
        this.inVoyn = inVoyn;
    }

    public String getAbbrVsim() {
        return abbrVsim;
    }

    public void setAbbrVsim(String abbrVsim) {
        this.abbrVsim = abbrVsim;
    }

    public String getInVoyn() {
        return inVoyn;
    }

    public void setInVoyn(String inVoyn) {
        this.inVoyn = inVoyn;
    }
}
