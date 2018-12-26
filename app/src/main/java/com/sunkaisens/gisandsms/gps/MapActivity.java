package com.sunkaisens.gisandsms.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.contact.ContactActivity;
import com.sunkaisens.gisandsms.event.MessageEvent;
import com.sunkaisens.gisandsms.utils.SpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author sun
 */
public class MapActivity extends AppCompatActivity implements OfflineMapManager.OfflineMapDownloadListener, AMapLocationListener, HintDialogFragment.DialogFragmentCallback {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        checkLocationPermission();

        ButterKnife.bind(this);
        if (aMap == null) {
            aMap = this.map.getMap();
        }
        map.onCreate(savedInstanceState);

        addMarker("15501129866", 0, 0);


        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为t


    }

    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private void checkLocationPermission() {
        // 检查是否有定位权限
        // 检查权限的方法: ContextCompat.checkSelfPermission()两个参数分别是Context和权限名.
        // 返回PERMISSION_GRANTED是有权限，PERMISSION_DENIED没有权限
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限，向系统申请该权限。
            Log.i("MY", "没有权限");
            requestPermission(LOCATION_PERMISSION_CODE);
        } else {
            //已经获得权限，则执行定位请求。
            Toast.makeText(MapActivity.this, "已获取定位权限", Toast.LENGTH_SHORT).show();

            //开启位置上报
            startLocation();

        }
    }

    private void requestPermission(int permissioncode) {
        String permission = getPermissionString(permissioncode);
        if (!IsEmptyOrNullString(permission)) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                    permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                if (permissioncode == LOCATION_PERMISSION_CODE) {
                    DialogFragment newFragment = HintDialogFragment.newInstance(R.string.location_description_title,
                            R.string.location_description_why_we_need_the_permission,
                            permissioncode);
                    newFragment.show(getFragmentManager(), HintDialogFragment.class.getSimpleName());
                } else if (permissioncode == STORAGE_PERMISSION_CODE) {
                    DialogFragment newFragment = HintDialogFragment.newInstance(R.string.storage_description_title,
                            R.string.storage_description_why_we_need_the_permission,
                            permissioncode);
                    newFragment.show(getFragmentManager(), HintDialogFragment.class.getSimpleName());
                }


            } else {
                Log.i("MY", "返回false 不需要解释为啥要权限，可能是第一次请求，也可能是勾选了不再询问");
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{permission}, permissioncode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapActivity.this, "定位权限已获取", Toast.LENGTH_SHORT).show();
                    Log.i("MY", "定位权限已获取");
                } else {
                    Toast.makeText(MapActivity.this, "定位权限被拒绝", Toast.LENGTH_SHORT).show();
                    Log.i("MY", "定位权限被拒绝");
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        DialogFragment newFragment = HintDialogFragment.newInstance(R.string.location_description_title,
                                R.string.location_description_why_we_need_the_permission,
                                requestCode);
                        newFragment.show(getFragmentManager(), HintDialogFragment.class.getSimpleName());
                        Log.i("MY", "false 勾选了不再询问，并引导用户去设置中手动设置");

                        return;
                    }
                }
                return;
            }
            case STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapActivity.this, "存储权限已获取", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "存储权限被拒绝", Toast.LENGTH_SHORT).show();
                    Log.i("MY", "定位权限被拒绝");
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        DialogFragment newFragment = HintDialogFragment.newInstance(R.string.storage_description_title,
                                R.string.storage_description_why_we_need_the_permission,
                                requestCode);
                        newFragment.show(getFragmentManager(), HintDialogFragment.class.getSimpleName());
                        Log.i("MY", "false 勾选了不再询问，并引导用户去设置中手动设置");
                    }
                    return;
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {

        boolean startLocation = SpUtil.getSpUtil(this).getBoolean(GlobalVar.LOCATION, false);
        if (startLocation) {
            startService(new Intent(this, GpsService.class));
        }

        Log.i("sjy", "startLocation");
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
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

    @Override
    public void doPositiveClick(int requestCode) {
        String permission = getPermissionString(requestCode);
        if (!IsEmptyOrNullString(permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{permission},
                        requestCode);
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    @Override
    public void doNegativeClick(int requestCode) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        int unReadSms = getUnReadSms();
        if (unReadSms == 0) {
            unreadMessageIcon.setVisibility(View.GONE);
        } else {
            unreadMessageIcon.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 下载地图
     */
    private void downloadOffMap() {


        OfflineMapManager manager = new OfflineMapManager(this, this);

        try {
            manager.downloadByCityCode("000");
            manager.downloadByCityCode("010");
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
     */
    @Override
    public void onCheckUpdate(boolean hasNew, String name) {

    }

    /**
     * 当调用OfflineMapManager.remove(String)方法时，如果有设置监听，会回调此方法
     * 当删除省份时，该方法会被调用多次，返回省份内城市删除情况。
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
                //进入系统短信列表界面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("smsto:" + "15501129866"));
                intent.setType("vnd.android-dir/mms-sms");
                startActivity(intent);
                break;
            //点击联系人
            case R.id.contact:
                Intent contactIntent = new Intent(this, ContactActivity.class);

                startActivity(contactIntent);

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
        //显示未读消息提示
        unreadMessageIcon.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, ContactActivity.REQUEST_CALL_PHONE_PERMISSION);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
        }
        return super.onKeyDown(keyCode, event);
    }
}
