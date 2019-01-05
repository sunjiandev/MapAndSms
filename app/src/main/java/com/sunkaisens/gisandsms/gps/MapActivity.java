package com.sunkaisens.gisandsms.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.MainActivity;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.RuntimeRationale;
import com.sunkaisens.gisandsms.event.ContactLocation;
import com.sunkaisens.gisandsms.event.MessageEvent;
import com.sunkaisens.gisandsms.event.ServerInfo;
import com.sunkaisens.gisandsms.sms.ReceiveSmsService;
import com.sunkaisens.gisandsms.sms.SMSMethod;
import com.sunkaisens.gisandsms.utils.BaseUtils;
import com.sunkaisens.gisandsms.utils.OffLineMapUtils;
import com.sunkaisens.gisandsms.utils.SpUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sunkaisens.gisandsms.tabcontact.ContactFragment.REQUEST_CALL_PHONE_PERMISSION;

/**
 * @author sun
 */
public class MapActivity extends AppCompatActivity implements OfflineMapManager.OfflineMapDownloadListener, AMapLocationListener {

    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.message)
    ImageView message;
    @BindView(R.id.contact)
    ImageView contact;
    @BindView(R.id.unread_message_icon)
    ImageView unreadMessageIcon;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;

    private String[] permissions = {Permission.ACCESS_FINE_LOCATION, Permission.RECEIVE_SMS,
            Permission.READ_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE, Permission.CALL_PHONE, Permission.SEND_SMS};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        map.onCreate(savedInstanceState);

        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);

        if (aMap == null) {
            aMap = this.map.getMap();
        }


        /***********************设置自己的定位点显示***************************/
        aMap.setMyLocationEnabled(true);
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置为


        getFirstData();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //开启服务器获短信
        startService(new Intent(this, ReceiveSmsService.class));

        checkLocationPermission();

        initMapPath();

    }

    /**
     * 第一次开机获取好友的当前位置
     */
    private void getFirstData() {


        //获取自己的号码
        ServerInfo localInfo = new ServerInfo();
        localInfo.setT(GlobalVar.SEND_MSG_TYPE.REQUEST_LOCAL_NUMBER);
        localInfo.setR("");
        localInfo.setU("");
        localInfo.setM("");
        localInfo.setB("");
        String localJsonStr = JSON.toJSONString(localInfo);
        SMSMethod.getInstance(this).SendMessage(GlobalVar.SMS_CENTER_NUMBER, localJsonStr);
        //开启gps服务

        boolean isOpen = SpUtil.getSpUtil(this).getBoolean(GlobalVar.LOCATION, false);
        Log.d("sjy", "is open gps location service" + isOpen);

        if (isOpen) {
            startService(new Intent(this, GpsService.class));
        }

    }

    private void initMapPath() {
        String deviceId = BaseUtils.getInstance().getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            if (!deviceId.startsWith("860000")) {
                System.exit(0);
            }
        }
    }

    private void checkLocationPermission() {

        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {

                        boolean startService = SpUtil.getSpUtil(MapActivity.this).getBoolean(GlobalVar.LOCATION, false);
                        if (startService) {
                            startService(new Intent(MapActivity.this, GpsService.class));
                        }

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MapActivity.this, permissions)) {
                            showSettingDialog(MapActivity.this, permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("??")
                .setMessage(message)
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        Toast.makeText(MapActivity.this, "????", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }


    /**
     * 下载地图
     */
    private void downloadOffMap() {

        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);

        OfflineMapManager manager = new OfflineMapManager(this, this);
        OfflineMapCity nanjing = manager.getItemByCityCode("nanjing");
        Log.d("sjy", "nanjing code:" + nanjing);
        try {
            manager.downloadByCityCode("000");
            manager.downloadByCityCode("010");
            manager.downloadByCityName("nanjing");
        } catch (AMapException e) {
            e.printStackTrace();
        }


    }

    /**
     * 下载状态回调，在调用downloadByCityName 等下载方法的时候会启动
     */
    @Override
    public void onDownload(int status, int completeCode, String name) {

        Log.d("sjy", "name :" + name);
    }

    /**
     * 当调用updateOfflineMapCity 等检查更新函数的时候会被调用
     *
     * @Override
     */

    public void onCheckUpdate(boolean hasNew, String name) {

    }

    /**
     * 当调用OfflineMapManager.remove(String)方法时，如果有设置监听，会回调此方法
     * * 当删除省份时，该方法会被调用多次，返回省份内城市删除情况。
     */
    @Override
    public void onRemove(boolean success, String name, String describe) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            double longitude = aMapLocation.getLongitude();
            double latitude = aMapLocation.getLatitude();
            LatLng location = new LatLng(latitude, longitude);


        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
            Toast.makeText(this, errText, Toast.LENGTH_LONG).show();
        }
    }


    @OnClick({R.id.message, R.id.contact})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //查看的未读消息
            case R.id.message:
//                //进入系统短信列表界面
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.setData(Uri.parse("smsto:" + "115501129866"));
//                intent.setType("vnd.android-dir/mms-sms");
//                startActivity(intent);
                Intent chatIntent = new Intent(this, MainActivity.class);
                chatIntent.putExtra(GlobalVar.INTENT_DATA, 0);
                startActivity(chatIntent);
                unreadMessageIcon.setVisibility(View.GONE);
                break;
            //点击联系人
            case R.id.contact:
                Intent contactIntent = new Intent(this, MainActivity.class);
                contactIntent.putExtra(GlobalVar.INTENT_DATA, 1);
                startActivity(contactIntent);

                break;
            default:
                break;
        }
    }

    /**
     * 收到推送的sms消息
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSMS(MessageEvent message) {
        Log.d("sjy", "receive event bus event :" + message.toString());
        unreadMessageIcon.setVisibility(View.VISIBLE);

    }

    /**
     * 多个用户的位置推送
     *
     * @param contactLocations 位置数据集合
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveLocations(List<ContactLocation> contactLocations) {
        Log.d("sjy", "receive locations event :" + message.toString());
        //显示未读消息提示
        for (ContactLocation location : contactLocations) {
            addMarker(location.getU(), Float.valueOf(location.getLat()), Float.valueOf(location.getLon()));
        }

    }

    /**
     * 单个用户的位置推送
     *
     * @param location 位置数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveLocation(final ContactLocation location) {
        Log.d("sjy", "receive locations event :" + message.toString());


        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();

        Log.d("sjy", "get screen markers list :" + mapScreenMarkers.size());

        for (int i = 0; i < mapScreenMarkers.size(); i++) {
            Marker marker = mapScreenMarkers.get(i);
            Log.d("sjy", "get marker snippet :" + marker.getSnippet());
            if (marker.getSnippet() != null)
                if (marker.getSnippet().equals(location.getU())) {
                    Log.d("sjy", "remove marker");
                    marker.remove();
                }
        }


        addMarker(location.getU(), Float.valueOf(location.getLat()), Float.valueOf(location.getLon()));
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(new Intent(this, GpsService.class));
    }

    /**
     * 获取未读短信的数量
     *
     * @return 数量
     */
    private int getUnReadSms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            Log.e("sjy", "未授权");
        } else {
            int result = 0;
            Cursor csr = getContentResolver().query(Uri.parse("content://sms"), null,
                    "type = 1 and read = 0", null, null);
            if (csr != null) {
                result = csr.getCount();
                csr.close();
            }
            return result;
        }
        return 0;
    }

    private void addMarker(String number, float lan, float lon) {

        LatLng sourceLatLng = new LatLng(lan, lon);
        CoordinateConverter converter = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();

        aMap.addMarker(new MarkerOptions().position(desLatLng).snippet(number));
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String number = marker.getSnippet();
                //显示业务的操作方式
                showServerType(number);
                return false;
            }
        });

    }

    private void showServerType(final String number) {

        new AlertDialog.Builder(this).setTitle("选择业务").setItems(new String[]{"发送短消息", "拨打电话"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    //发送短信
                    case 0:
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                        sendIntent.setData(Uri.parse("smsto:" + number));
                        sendIntent.putExtra("sms_body", "");
                        MapActivity.this.startActivity(sendIntent);
                        break;
                    //拨打电话
                    case 1:
                        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("sjy", "未授权");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                            }
                            return;
                        } else {
                            call(number);
                        }


                        break;
                    default:
                        break;
                }
            }
        }).create().show();
    }

    @SuppressLint("MissingPermission")
    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        startActivity(intent);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
