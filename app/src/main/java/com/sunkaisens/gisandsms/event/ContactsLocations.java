package com.sunkaisens.gisandsms.event;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/29
 * @email:sunjianyun@sunkaisens.com
 * @Description: 返回所有好友的群组数据
 */
public class ContactsLocations {

    /**
     * code : 200
     * message : 数据获取成功
     * data : [{"u":"19800000009","lon":"120.4233","lat":"34.532","mTime":"2018-50-28  17:50:05"}]
     */

    private String code;
    private String message;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * u : 19800000009
         * lon : 120.4233
         * lat : 34.532
         * mTime : 2018-50-28  17:50:05
         */

        private String u;
        private String lon;
        private String lat;
        private String mTime;

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

        public String getMTime() {
            return mTime;
        }

        public void setMTime(String mTime) {
            this.mTime = mTime;
        }
    }
}
