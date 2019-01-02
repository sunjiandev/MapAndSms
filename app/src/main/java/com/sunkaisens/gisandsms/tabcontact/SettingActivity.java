package com.sunkaisens.gisandsms.tabcontact;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseActivity;
import com.sunkaisens.gisandsms.event.MapConfigEvent;
import com.sunkaisens.gisandsms.gps.GpsService;
import com.sunkaisens.gisandsms.utils.SpUtil;
import com.sunkaisens.gisandsms.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;

/**
 * @author:sjy
 * @date:2018/12/25
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getCanonicalName();
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.location_icon)
    ImageView locationIcon;
    @BindView(R.id.switch_location)
    SwitchCompat switchLocation;
    @BindView(R.id.location_layout)
    CardView locationLayout;
    @BindView(R.id.location_tips)
    TextView locationTips;
    @BindView(R.id.time_icon)
    ImageView timeIcon;
    @BindView(R.id.distance_spinner)
    Spinner distanceSpinner;
    @BindView(R.id.distance_layout)
    CardView distanceLayout;
    @BindView(R.id.distance_tips)
    TextView distanceTips;
    @BindView(R.id.distance_icon)
    ImageView distanceIcon;
    @BindView(R.id.time_spinner)
    Spinner timeSpinner;
    @BindView(R.id.time_layout)
    CardView timeLayout;

    @Override
    protected void initView() {


        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SpUtil.getSpUtil(SettingActivity.this).putBoolean(GlobalVar.LOCATION, isChecked);
                if (isChecked) {
                    startService(new Intent(SettingActivity.this, GpsService.class));
                } else {
                    stopService(new Intent(SettingActivity.this, GpsService.class));
                }

            }
        });


        //初始化 上报位置的距离spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.post_distance));
        distanceSpinner.setAdapter(arrayAdapter);


        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SpUtil.getSpUtil(SettingActivity.this).putInt(GlobalVar.POST_DISTANCE, position);
                MapConfigEvent mapConfigEvent = new MapConfigEvent(GlobalVar.TYPE_POST_DISTANCE, position);
                EventBus.getDefault().post(mapConfigEvent);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //初始化上报时间间隔的spinner

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.upload_time));
        timeSpinner.setAdapter(timeAdapter);


        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SpUtil.getSpUtil(SettingActivity.this).putInt(GlobalVar.UPLOAD_TIME, position);
                MapConfigEvent mapConfigEvent = new MapConfigEvent(GlobalVar.TYPE_UPLOAD_TIME, position);
                EventBus.getDefault().post(mapConfigEvent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initData();


    }

    private void initData() {

        int position = SpUtil.getSpUtil(this).getInt(GlobalVar.POST_DISTANCE, 3);
        distanceSpinner.setSelection(position, true);

        boolean isOpen = SpUtil.getSpUtil(this).getBoolean(GlobalVar.LOCATION, false);
        switchLocation.setChecked(isOpen);

        int uploadTime = SpUtil.getSpUtil(this).getInt(GlobalVar.UPLOAD_TIME, 3);
        timeSpinner.setSelection(uploadTime, true);


//        downloadNewVersion("http://mhhy.dl.gxpan.cn/apk/ml/MBGYD092101/Gardenscapes-ledou-MBGYD092101.apk");

    }

    private void downloadNewVersion(String uri) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "wifi.apk");
        //设置现在的文件可以被MediaScanner扫描到。
        request.allowScanningByMediaScanner();
        //设置通知的标题
        request.setTitle("版本更新");
        //设置下载的时候Notification的可见性。       r
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置下载文件类型
        request.setMimeType("application/vnd.android.package-archive");

        long id = downloadManager.enqueue(request);
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = downloadManager.query(query.setFilterById(id));

        if (cursor != null && cursor.moveToFirst()) {
            //已经下载的字节数
            int bytesDownload = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            //文件的总的字节数
            int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            if (bytesDownload == bytesTotal) {
                ToastUtils.showToast(this, "下载完成");
                //开始安装
                String downloads = Environment.DIRECTORY_DOWNLOADS + "/wifi.apk";
                install(downloads);
            }
        }
    }

    private void install(String filePath) {
        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    this
                    , "com.sunkaisens.gisandsms.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }


    @Override
    protected int initLayout() {
        return R.layout.activity_setting;
    }
}
