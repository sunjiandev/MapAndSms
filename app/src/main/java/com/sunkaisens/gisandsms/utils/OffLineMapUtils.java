package com.sunkaisens.gisandsms.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class OffLineMapUtils {
    /**
     * 获取map 缓存和读取目录
     */
    public static String getSdCacheDir(Context context) {

        File storageDirectory = Environment.getExternalStorageDirectory();

        File gismap = new File(storageDirectory, "gismap");

        if (!gismap.exists()) {
            gismap.mkdir();
        }
        Log.d("sjy", "create offmap path :" + gismap.getAbsolutePath());
        return gismap.getAbsolutePath();


    }
}