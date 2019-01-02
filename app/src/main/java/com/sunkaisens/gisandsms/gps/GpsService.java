package com.sunkaisens.gisandsms.gps;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.event.MapConfigEvent;
import com.sunkaisens.gisandsms.event.SMSLocation;
import com.sunkaisens.gisandsms.event.ServerInfo;
import com.sunkaisens.gisandsms.sms.SMSMethod;
import com.sunkaisens.gisandsms.utils.BaseUtils;
import com.sunkaisens.gisandsms.utils.SpUtil;
import com.sunkaisens.gisandsms.utils.ThreadPoolProxy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @author sjy
 * @date 2018-12-25
 */
public class GpsService extends Service implements Runnable {
    ArrayList<CellInfo> cellIds = null;
    private Gps gps = null;
    private boolean threadDisable = false;
    private final static String TAG = GpsService.class.getSimpleName();
    private UtilTool utilTool;

    /**
     * 默认上报时间是2min
     */
    private int uploadTime = 1000;
    /**
     * 保存前一个点的经度
     */
    private double oldLatitude;
    /**
     * 前一个点的纬度
     */
    private double oldLongitude;


    /**
     * 上报位置的距离 默认是50m
     */

    private int uploadDistance = 50;

    /**
     * 上报位置计数,距离大于50 m上报一次位置,如果长时间不动,那么 120s上班一次
     */

    private int uploadCount;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10001:
                    float distance = (float) msg.obj;
//                    ToastUtils.showToast(GpsService.this, "上报位置的距离:" + distance);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initData();

        gps = new Gps(GpsService.this);
        utilTool = UtilTool.getUtilTool(GpsService.this);
        utilTool.init(this);
        //开始获取经纬度信息
        ThreadPoolProxy.getInstence(null).executeTask(this);

        EventBus.getDefault().register(this);

    }

    /**
     * 初始化上报位置信息的数据
     */
    private void initData() {
        int uploadDistancePosition = SpUtil.getSpUtil(this).getInt(GlobalVar.POST_DISTANCE, 3);
        Log.d("sjy", "get save upload distance position");
        String[] distanceArray = getResources().getStringArray(R.array.post_distance);
        uploadDistance = Integer.valueOf(distanceArray[uploadDistancePosition]);
        Log.d("sjy", "set upload distance :" + uploadDistance);

        int uploadTimePosition = SpUtil.getSpUtil(this).getInt(GlobalVar.UPLOAD_TIME, 0);
        Log.d("sjy", "get save upload time position");
        String[] timeArray = getResources().getStringArray(R.array.upload_time);
        //转换成秒
        uploadTime = Integer.valueOf(timeArray[uploadTimePosition]) * 60;
        Log.d("sjy", "set upload location time :" + uploadTime);
    }

    @Override
    public void onDestroy() {
        threadDisable = true;
        if (cellIds != null && cellIds.size() > 0) {
            cellIds = null;
        }
        if (gps != null) {
            gps.closeLocation();
            gps = null;
        }
        ThreadPoolProxy.getInstence(null).removeTask(this);

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void run() {
        while (!threadDisable) {
            SystemClock.sleep(5 * 1000);
            uploadCount++;
            //当结束服务时gps为空
            if (gps != null) {
                //获取经纬度
                Location location = gps.getLocation();
                //如果gps无法获取经纬度，改用基站定位获取
                if (location == null) {
                    Log.d("sjy", "gps location null");
                    //2.根据基站信息获取经纬度
                    try {
                        location = utilTool.callGear(GpsService.this, cellIds);
                    } catch (Exception e) {
                        location = null;
                        e.printStackTrace();
                        Log.d("sjy", "获取经纬度信息失败");
                    }
                    if (location == null) {
                        Log.d("sjy", "x");
                        return;
                    }
                }


                //实时位置的点
                LatLng currentLatng = new LatLng(location.getLatitude(), location.getLongitude());
                //上一个位置的点
                LatLng oldLatng = new LatLng(oldLatitude, oldLongitude);

                //计算两点之间的直线距离
                float distance = AMapUtils.calculateLineDistance(currentLatng, oldLatng);
                Log.d("sjy", "two point distance :" + distance);
                Log.d("sjy", "upload location count :" + uploadCount);


                ServerInfo info = new ServerInfo();
                info.setU(BaseUtils.getInstance().getLocalNumber(this));
                String strLo = JSON.toJSONString(new SMSLocation(location.getLatitude(), location.getLongitude(), BaseUtils.getInstance().getLocalNumber(this)));
                info.setB(strLo);
                info.setM("POST");
                info.setR(GlobalVar.REQUEST_API);
                info.setT(GlobalVar.SEND_MSG_TYPE.GIS_MSG);
                String strJson = JSON.toJSONString(info);
                Log.d("sjy", "json str :" + strJson);
                SMSMethod.getInstance(this).SendMessage(GlobalVar.SMS_CENTER_NUMBER, strJson);


                Message msg = Message.obtain();
                msg.what = 10001;
                msg.obj = distance;
                handler.sendMessage(msg);

                //两点之间的距离大于设定的距离,则上班位置
                if (distance >= uploadDistance) {
                    uploadCount = 0;
                    // TODO: 2018/12/26 发送上报位置的短信
                    Log.d("sjy", "distance greater than config ,upload location info");
                    //发送完成之后保存上一次的位置信息
                    //纬度
                    oldLatitude = location.getLatitude();
                    //经度
                    oldLongitude = location.getLongitude();

                } else if (uploadCount >= uploadTime) {
                    // TODO: 2018/12/26 发送上报位置的短信
                    uploadCount = 0;
                    Log.d("sjy", "time greater than config ,upload location info");
                    //纬度
                    oldLatitude = location.getLatitude();
                    //经度
                    oldLongitude = location.getLongitude();
//                    ServerInfo info = new ServerInfo();
//                    info.setU(BaseUtils.getInstance().getLocalNumber(this));
//                    String strLo = JSON.toJSONString(new SMSLocation(location.getLatitude(), location.getLongitude(), BaseUtils.getInstance().getLocalNumber(this)));
//                    info.setB(strLo);
//                    info.setR(GlobalVar.UPLOAD_LOCATION_HEADER);
//                    String strJson = JSON.toJSONString(info);
//                    Log.d("sjy", "json str :" + strJson);
//                    SMSMethod.getInstance(this).SendMessage(GlobalVar.SMS_CENTER_NUMBER, strJson);

                }


                Log.d("sjy", "获取到的经度：      " + oldLongitude);
                Log.d("sjy", "获取到的纬度：      " + oldLatitude);

            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MapConfigChange(MapConfigEvent event) {

        int position = event.getPosition();

        switch (event.getType()) {
            //上报位置的距离发生变化
            case GlobalVar.TYPE_POST_DISTANCE:

                uploadCount = 0;
                String[] distanceArray = getResources().getStringArray(R.array.post_distance);
                uploadDistance = Integer.valueOf(distanceArray[position]);
                Log.d("sjy", "set upload distance event:" + uploadDistance);

                break;
            //上班位置的时间发送变化
            case GlobalVar.TYPE_UPLOAD_TIME:
                uploadCount = 0;
                String[] timeArray = getResources().getStringArray(R.array.upload_time);
                uploadTime = Integer.valueOf(timeArray[position]) * 60;//转换成秒
                Log.d("sjy", "set upload location time event:" + uploadTime);
                break;
            default:
                break;
        }
    }
}