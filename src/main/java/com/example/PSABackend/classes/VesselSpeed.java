package com.example.PSABackend.classes;

import java.util.ArrayList;

public class VesselSpeed {
    private boolean increasing;

    public void setIncrasing(boolean increasing) {
        this.increasing = increasing;
    }

    public void setList(ArrayList<Double> list) {
        this.list = list;
    }

    public boolean isIncrasing() {
        return increasing;
    }

    public ArrayList<Double> getList() {
        return list;
    }

    private ArrayList<Double> list;
}
