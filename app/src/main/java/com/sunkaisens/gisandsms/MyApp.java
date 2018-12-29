package com.sunkaisens.gisandsms;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * @author:sun
 * @date:2018/12/24
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MyApp extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
