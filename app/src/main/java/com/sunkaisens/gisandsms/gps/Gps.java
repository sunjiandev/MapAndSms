package com.sunkaisens.gisandsms.gps;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

/**
 * @author sjy
 * @date 2018-12-25
 */
public class Gps {
    private Location location;
    private LocationManager locationManager;
    private Context context ;
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    
    private static final String TAG =  Gps.class.getCanonicalName();



    /**
     * 初始化
     *
     * @param context 上下文
     */
    Gps(Context context) {
        this.context = context;
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this.context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限，向系统申请该权限。
            Log.i(TAG, "没有权限");

        } else {
            //已经获得权限，则执行定位请求。
            Log.d("sjy", "已经获得");
            location = locationManager.getLastKnownLocation(getProvider());
            List<String> allProviders = locationManager.getAllProviders();
            for (int i = 0; i < allProviders.size(); i++) {
                Log.d("sjy", "拿到所有的Providers：===" + allProviders.get(i
                ));
                if (location==null){
                    location = locationManager.getLastKnownLocation(allProviders.get(i));
                }
            }
            Log.d("sjy", "locations是不是null" + location
            );
            Log.d("sjy", "gps初始化");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        }

    }

    private String getPermissionString(int requestCode) {
        String permission = "";
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case STORAGE_PERMISSION_CODE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
        }
        return permission;
    }


    // 获取Location Provider
    private String getProvider() throws IllegalArgumentException{
        Log.d("sjy", "获取位置提供者");
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        Log.d("sjy", "返回的信息:              " + locationManager.getBestProvider(criteria, true));
        // 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
        return locationManager.getBestProvider(criteria, true);
    }

    private LocationListener locationListener = new LocationListener() {
        // 位置发生改变后调用
        public void onLocationChanged(Location l) {
            if (l != null) {
                location = l;
            }
        }

        // provider 被用户关闭后调用
        public void onProviderDisabled(String provider) {
            location = null;
        }

        // provider 被用户开启后调用
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l != null) {
                location = l;
            }

        }

        // provider 状态变化时调用
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    public Location getLocation() {
        return location;
    }

    public void closeLocation() {
        if (locationManager != null) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
                locationListener = null;
            }
            locationManager = null;
        }
    }


}