package com.example.michaeleliot.interstorydrift;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by michaeleliot on 1/31/18.
 */

public class Floor {
    private String floorName;
    private double x_sway;
    private double y_sway;
    private double z_sway;
    private int floorNumber;

    public Floor() {
    }

    public Floor(int floorNumber, String floorName, double x_sway, double y_sway, double z_sway) {
        this.floorName = floorName;
        this.floorNumber = floorNumber;
        this.x_sway = x_sway;
        this.y_sway = y_sway;
        this.z_sway = z_sway;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String name) {
        this.floorName = floorName;
    }

    public double getXSway() {
        return x_sway;
    }

    public void setXSway(double x_sway) {
        this.x_sway = x_sway;
    }

    public double getYSway() {
        return y_sway;
    }

    public void setYSway(double y_sway) {
        this.y_sway = y_sway;
    }

    public double getZSway() {
        return z_sway;
    }

    public void setZSway(double z_sway) {
        this.z_sway = z_sway;
    }


}
