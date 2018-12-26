package com.sunkaisens.gisandsms.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author :sjy
 * 邮箱:sunjianyun@sunkaisens.com
 * 时间:2018/3/22 16:04
 */

public abstract class BaseFragment extends Fragment {
    private Unbinder unbinder;
private Toast toast;
    private boolean isUnbinder = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View view = View.inflate(container.getContext(), initLayout(), null);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    /**
     * 初始化控件
     *
     * @param view 布局
     */
    protected abstract void initView(View view);

    /**
     * 初始化布局
     *
     * @return
     */
    protected abstract int initLayout();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

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
            startActivity(new Intent(getContext(), cls));
        } else {
            showToast(getString(R.string.net_err));
        }

    }

    /**
     * 跳转到指定Activity,携带String传值
     */
    public void go(Class<?> cls, String extra) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getContext(), cls).putExtra(GlobalVar.INTENT_DATA, extra));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity,携带String传值
     */
    public void go(Class<?> cls, String extraKey, String extraValue) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getContext(), cls).putExtra(extraKey, extraValue));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    /**
     * 跳转到指定Activity,携带String传值
     */
    public void go(Class<?> cls, String extraKey1, String extraValue1, String extraKey2, String extraValue2) {
        if (GlobalVar.NETWORKISCONNNECT) {

            startActivity(new Intent(getContext(), cls).putExtra(extraKey1, extraValue1).putExtra(extraKey2, extraValue2));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    public void go(Class<?> cls, Bundle bundle) {
        if (GlobalVar.NETWORKISCONNNECT) {
            startActivity(new Intent(getContext(), cls).putExtras(bundle));
        } else {
            showToast(getString(R.string.net_err));
        }
    }

    public void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
