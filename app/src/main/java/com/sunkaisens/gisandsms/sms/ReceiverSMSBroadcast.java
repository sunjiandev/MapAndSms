package com.sunkaisens.gisandsms.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.sunkaisens.gisandsms.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author:sun
 * @date:2018/12/20
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ReceiverSMSBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String phone = "10086";

        String num, con;
        //读取data中存入的安全号码
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] objs = (Object[]) bundle.get("pdus");
            SmsMessage[] smsMessages = new SmsMessage[objs.length];
            for (int i = 0; i < objs.length; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
                //短信的号码
                num = smsMessages[i].getDisplayOriginatingAddress();
                //短信的内容
                con = smsMessages[i].getDisplayMessageBody();

                Log.d("sjy", "broad cast receive sms content is :" + con);
                Log.d("sjy", "broad cast receive sms number is :" + num);
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setContent(con);
                messageEvent.setNumber(num);
                EventBus.getDefault().post(messageEvent);
            }
        }
    }

}
