package com.sunkaisens.gisandsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunkaisens.gisandsms.utils.ToastUtils;

/**
 * @author:sun
 * @date:2018/12/26
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class DownloadFinishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ToastUtils.showToast(context, "文件下载完成");

    }
}
