package com.sunkaisens.gisandsms.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.event.LastMessageSMS;
import com.sunkaisens.gisandsms.event.MessageEvent;
import com.sunkaisens.gisandsms.event.MessageSMS;
import com.sunkaisens.gisandsms.utils.BaseUtils;
import com.sunkaisens.gisandsms.utils.ListenerHelper;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.UUID;

/**
 * @author:sun
 * @date:2018/12/20
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ReceiverSMSBroadcast extends BroadcastReceiver {
    private String num, con;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("sjy", "intent get action :" + intent.getAction());
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
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

                    String s = smsMessages[i].toString();

                    Log.d("sjy", "broad cast receive sms content is :" + con);
                    Log.d("sjy", "broad cast receive sms number is :" + num);
                    Log.d("sjy", "broad cast receive sms to string is :" + s);
                    LastMessageSMS lastMessageSMS = new LastMessageSMS();
                    lastMessageSMS.setData(System.currentTimeMillis());
                    lastMessageSMS.setLastSMS(con);
                    lastMessageSMS.setRemoteNumber(num);
                    String localNumber = BaseUtils.getInstance().getLocalNumber();


                    List<LastMessageSMS> list = DataSupport.where("localNumber = ? and remoteNumber = ?", localNumber, num).find(LastMessageSMS.class);
                    //更新
                    if (list != null && list.size() != 0) {
                        lastMessageSMS.updateAll("localNumber = ? and remoteNumber = ?", localNumber, num);
                        ListenerHelper.executeOnMessageUpdate(lastMessageSMS);
                        //新存
                    } else {
                        lastMessageSMS.setRemoteNumber(num);
                        lastMessageSMS.setLocalNumber(BaseUtils.getInstance().getLocalNumber());
                        lastMessageSMS.save();

                        ListenerHelper.executeOnReceiveNewMessageSms(lastMessageSMS);
                    }
                    saveSms(num, con,context);
                    //收到数据之后,先本地存,然后发送通知
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setContent(con);
                    messageEvent.setNumber(num);
                    EventBus.getDefault().post(messageEvent);
                }
            }
        }
    }

    /**
     * 保存聊天内容
     *
     * @param num
     * @param con
     */
    private void saveSms(String num, String con,Context context) {
        MessageSMS messageSMS = new MessageSMS();
        messageSMS.setIsRead(1);
        messageSMS.setMsgType(GlobalVar.IN_TEXT_MESSAGE);
        messageSMS.setLocalAccount(BaseUtils.getInstance().getLocalNumber());
        messageSMS.setLocalMsgID(UUID.randomUUID().toString());
        messageSMS.setRemoteAccount(num);
        messageSMS.setStartTime(System.currentTimeMillis());
        messageSMS.setMsg(con);
        messageSMS.save();

        EventBus.getDefault().post(messageSMS);
    }

}
