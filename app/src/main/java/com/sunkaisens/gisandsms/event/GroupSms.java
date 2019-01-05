package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2019/1/3
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class GroupSms {
    private String n;
    private String c;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
    
    @Override
    public String toString() {
        return "GroupSms{" +
                "n='" + n + '\'' +
                ", c='" + c + '\'' +
                '}';
    }
}
