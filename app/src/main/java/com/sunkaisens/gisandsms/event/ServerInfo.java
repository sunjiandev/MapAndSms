package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2018/12/28
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ServerInfo {


    /**
     * r : 0100
     * b : {"lat":39.82872694,"lon":116.27783708}
     * u : 15381000298
     */

    private String m;
    private String t;
    private String r;
    private String b;
    private String u;


    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }
}
