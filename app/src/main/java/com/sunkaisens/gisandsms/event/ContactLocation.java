package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2018/12/29
 * @email:sunjianyun@sunkaisens.com
 * @Description: 单个用户的位置
 */
public class ContactLocation {


    /**
     * u : 19800000009
     * lon : 120.4233
     * lat : 34.532
     * mTime : 2018-50-28  17:50:05
     */

    private String u;
    /**
     * 经度 大
     */
    private String lon;
    /**
     * 纬度 小
     */
    private String lat;


    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

}
