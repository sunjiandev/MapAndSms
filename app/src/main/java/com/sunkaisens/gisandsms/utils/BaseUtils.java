package com.sunkaisens.gisandsms.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.sunkaisens.gisandsms.MyApp;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 作者:sjy
 * 邮箱:sunjianyun@sunkaisens.com
 * 时间:2018/6/22 14:30
 * <p>
 *
 * @author sun
 */

public class BaseUtils {
    private static BaseUtils baseUtils;
    private static final String TAG = BaseUtils.class.getCanonicalName();

    private List<Activity> activityCompats = new ArrayList<>();
    private final AudioManager audioManager;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager.WakeLock wl;
    private String localNumber = "";

    private BaseUtils() {

        audioManager = (AudioManager) MyApp.getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public static BaseUtils getInstance() {
        if (baseUtils == null) {
            synchronized (BaseUtils.class) {
                if (baseUtils == null) {
                    baseUtils = new BaseUtils();
                }
            }
        }
        return baseUtils;
    }

    /**
     * 格式化时间
     *
     * @param time long型的时间值
     * @return 返回时间类型
     */
    public String formatLongTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String format = simpleDateFormat.format(date);
        return format;
    }

    public void initActivity(Activity activityCompat) {
        activityCompats.add(activityCompat);
    }


    public void switchAccount() {
        if (activityCompats != null && activityCompats.size() != 0) {
            for (Activity activityCompat : activityCompats) {
                Log.d(TAG, "stop activity is :" + activityCompat.getClass().getCanonicalName());
                activityCompat.finish();
            }
        }
    }

    /**
     * 判断某个界面是否在前台
     */
    public boolean isForeground(String className) {

        if (TextUtils.isEmpty(className)) {
            return false;
        }
        Context context = MyApp.getContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * MD5加密
     * <br>http://stackoverflow.com/questions/1057041/difference-between-java-and-php5-md5-hash
     * <br>http://code.google.com/p/roboguice/issues/detail?id=89
     */
    public String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        int len = md5code.length();
        for (int i = 0; i < 32 - len; i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 返回可逆算法DES的密钥
     *
     * @param key 前8字节将被用来生成密钥。
     * @return 生成的密钥
     * @throws Exception
     */
    public Key getDESKey(byte[] key) throws Exception {
        DESKeySpec des = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(des);
    }

    /**
     * 保持屏幕常亮
     *
     * @param activity 需要保持常亮的Activity
     */
    public void keepScreenLongLight(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     * 获取AudioManager的实例
     *
     * @return
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }


    /**
     * 根据uri获取路径
     *
     * @param context 上下文
     * @param uri     文件的链接
     * @return 文件的路径
     */
    public String getRealPathFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    public int dip2px(int dp) {
        float density = MyApp.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    /**
     * px转换dip
     */
    public int px2dip(int px) {
        final float scale = MyApp.getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public String getLocalNumber() {
//        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return "";
//        } else {
//            String line1Number = manager.getLine1Number();
//            Log.d("sjy", "get my number :" + line1Number);
//
//            int simCountryIso = manager.getSimState();
//            Log.d("sjy", "get my simCountryIso :" + simCountryIso);
//            manager.getSimState();
//
//            if (line1Number == null) {
//                line1Number = "";
//            }
//            return line1Number;
//        }
        return localNumber;

    }

    /**
     * 获取手机的 device id
     *
     * @return
     */
    public String getDeviceId() {
        TelephonyManager manager = (TelephonyManager) MyApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String deviceId = manager.getDeviceId();
        Log.d("sjy", "get device id :" + deviceId);

        String deviceSoftwareVersion = manager.getDeviceSoftwareVersion();

        Log.d("sjy", "get soft version :" + deviceSoftwareVersion);

        return deviceId;
    }

    /**
     * 获取手机型号
     */
    public String getDeviceBuild() {

        String device = Build.DEVICE;
        Log.d("sjy", "get device build :" + device);
        return device;

    }

    public  String getBaseband_Ver() {
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String) result;
        } catch (Exception e) {
        }
        Log.d("sjy","get baseband version :"+Version);
        return Version;
    }

    /**
     * 设置自己的号码
     * @param localNumber 号码
     */
    public void setLocalNumber(String localNumber) {

        this.localNumber = localNumber;
    }
}
