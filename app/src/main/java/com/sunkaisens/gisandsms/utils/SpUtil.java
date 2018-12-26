package com.sunkaisens.gisandsms.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * description: Sp
 * created by kalu on 2016/11/19 11:58
 */
public class SpUtil {

    private static SpUtil spUtil;
    private static Context context;

    private SpUtil() {
    }

    public static SpUtil getSpUtil(Context context) {

        SpUtil.context = context;
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil();
                }
            }
        }
        return spUtil;

    }


    private static final String CONFIG_NAME = "config";
    private SharedPreferences pre;

    /**
     * 获得本地SharedPreferences的boolean数据
     *
     * @param key         得到数据的标识
     * @param defaltValue 如果获得不到的默认值
     * @return z值
     */
    public boolean getBoolean(String key, boolean defaltValue) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        return pre.getBoolean(key, defaltValue);
    }

    /**
     * 保存到本地SharedPreferences的boolean数据
     *
     * @param key   得到数据的标识
     * @param value 如果获得不到的默认值
     */
    public void putBoolean(String key, boolean value) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        pre.edit().putBoolean(key, value).apply();
    }


    /**
     * 获得本地SharedPreferences的String数据
     *
     * @param key         得到数据的标识
     * @param defaltValue 如果获得不到的默认值
     * @return 值
     */
    public String getsString(String key, String defaltValue) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        return pre.getString(key, defaltValue);
    }

    /**
     * 保存到本地SharedPreferences的String数据
     *
     * @param key   得到数据的标识
     * @param value 如果获得不到的默认值
     */
    public void putString(String key, String value) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        pre.edit().putString(key, value).apply();
    }

    /**
     * 获得本地SharedPreferences的int数据
     *
     * @param key         得到数据的标识
     * @param defaltValue 如果获得不到的默认值
     * @return
     */
    public int getInt(String key, int defaltValue) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        return pre.getInt(key, defaltValue);
    }

    /**
     * 保存到本地SharedPreferences的int数据
     *
     * @param key   得到数据的标识
     * @param value 如果获得不到的默认值
     */
    public void putInt(String key, int value) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        pre.edit().putInt(key, value).apply();
    }


    /**
     * 获得本地SharedPreferences的Float数据
     *
     * @param key         得到数据的标识
     * @param defaltValue 如果获得不到的默认值
     * @return
     */
    public Float getsFloat(String key, float defaltValue) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        return pre.getFloat(key, defaltValue);
    }

    /**
     * 保存到本地SharedPreferences的Float数据
     *
     * @param key   得到数据的标识
     * @param value 如果获得不到的默认值
     */
    public void putFloat(String key, float value) {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        pre.edit().putFloat(key, value).apply();
    }

    public void removeAll() {
        if (pre == null) {
            pre = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
        pre.edit().clear().apply();
    }
}
