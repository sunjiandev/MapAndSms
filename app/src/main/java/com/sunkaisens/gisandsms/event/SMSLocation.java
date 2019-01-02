package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2018/12/28
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class SMSLocation {
    private double lat;
    private double lon;
    private String u;

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public SMSLocation(double lat, double lon,String u) {
        this.lat = lat;
        this.lon = lon;
        this.u = u;
    }
}
