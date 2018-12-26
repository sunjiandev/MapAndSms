package com.sunkaisens.gisandsms.base;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.MyApp;
import com.sunkaisens.gisandsms.R;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @author :yangwenhao
 * 邮箱:yangwenhao@sunkaisens.com
 * 时间:2018/4/9 10:20
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getCanonicalName();
    private static Toast toast;
    protected Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(initLayout());
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化参数
     */
    protected abstract void initView();

    /**
     * 初始化布局
     *
     * @return 布局id
     */

    protected abstract int initLayout();


    /**
     * 跳转到指定Activity
     */
    public void go(Intent intent) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(intent);
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity
     */
    public void go(Class<?> cls) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(this, cls));
        } else {
            showToast(getString(R.string.net_err));
        }

    }

    /**
     * 跳转到指定Activity,携带String传值
     */
    public void go(Class<?> cls, String extra) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(this, cls).putExtra(GlobalVar.INTENT_DATA, extra));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity,携带String传值
     */
    public void go(Class<?> cls, String extraKey, String extraValue) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(this, cls).putExtra(extraKey, extraValue));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity,携带String传值
     */
    public void go(Class<?> cls, String extraKey1, String extraValue1, String extraKey2, String extraValue2) {
        if (GlobalVar.NETWORKISCONNNECT) {

            startActivity(new Intent(this, cls).putExtra(extraKey1, extraValue1).putExtra(extraKey2, extraValue2));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    public void go(Class<?> cls, Bundle bundle) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(this, cls).putExtras(bundle));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity,并且结束当前Activity
     */
    public void goThenKill(Class<?> cls) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getApplicationContext(), cls));
            finish();
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    public void goThenKill(Class<?> cls, String extraKey, String extraValue) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getApplicationContext(), cls).putExtra(extraKey, extraValue));
            finish();
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    public void goThenKill(Class<?> cls, String extraKey1, String extraValue1, String extraKey2, String extraValue2) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getApplicationContext(), cls).putExtra(extraKey1, extraValue1).putExtra(extraKey2, extraValue2));
            finish();
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    public void goForResult(Class<?> cls, int requestCode) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivityForResult(new Intent(getApplicationContext(), cls), requestCode);
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity,并且结束当前Activity,携带String传值
     */
    public void goThenKill(Class<?> cls, String extra) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getApplicationContext(), cls).putExtra(GlobalVar.INTENT_DATA, extra));
            finish();
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 控制View显示隐藏
     */
    public void changeView(int viewStatus, int... viewId) {
        if (null == viewId) {
            return;
        }
        for (int id : viewId) {
            findViewById(id).setVisibility(viewStatus);
        }
    }

    public void changeBackgroundColor(String color, int... viewId) {
        if (null == viewId) {
            return;
        }

        for (int id : viewId) {
            findViewById(id).setBackgroundColor(Color.parseColor(color));
        }
    }

    /**
     * View显示状态
     */
    public int getVisibility(int viewId) {
        return findViewById(viewId).getVisibility();
    }

    /**
     * 设置TextView文本
     */
    public void setTextInfo(int viewId, CharSequence text) {

        if (TextUtils.isEmpty(text)) {
            return;
        }

        View view = findViewById(viewId);

        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else if (view instanceof EditText) {
            ((EditText) view).setText(text);
        } else {
            Log.d(TAG, "setTextInfo ==> view != TextView");
        }
    }

    public void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 判断某个界面是否在前台
     */
    public static boolean isForeground(String className) {

        if (TextUtils.isEmpty(className)) return false;

        ActivityManager am = (ActivityManager) MyApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
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
     * 显示进度条对话框
     *
     * @param message 提示消息
     * @return dialog
     */
    public ProgressDialog showProgressDialog(String message) {

        ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(message);

        return progressDialog;
    }
}
