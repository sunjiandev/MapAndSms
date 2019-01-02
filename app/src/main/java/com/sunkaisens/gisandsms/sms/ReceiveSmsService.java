package com.sunkaisens.gisandsms.sms;

import android.app.Service;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author:sun
 * @date:2018/12/28
 * @email:sunjianyun@sunkaisens.com
 * @Description:接受短信的服务
 */
public class ReceiveSmsService extends Service {

    public static final Uri SMS_MESSAGE_URI = Uri.parse("content://sms");
    private static SmsDatabaseChaneObserver mSmsDBChangeObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerSmsDatabaseChangeObserver(this);
    }


    private static void registerSmsDatabaseChangeObserver(ContextWrapper contextWrapper) {
        //因为，某些机型修改rom导致没有getContentResolver
        try {
            mSmsDBChangeObserver = new SmsDatabaseChaneObserver(contextWrapper.getContentResolver(), new Handler());
            contextWrapper.getContentResolver().registerContentObserver(SMS_MESSAGE_URI, true, mSmsDBChangeObserver);
        } catch (Throwable b) {
        }
    }

    private static void unregisterSmsDatabaseChangeObserver(ContextWrapper contextWrapper) {
        try {
            contextWrapper.getContentResolver().unregisterContentObserver(mSmsDBChangeObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSmsDatabaseChangeObserver(this);
    }
}
