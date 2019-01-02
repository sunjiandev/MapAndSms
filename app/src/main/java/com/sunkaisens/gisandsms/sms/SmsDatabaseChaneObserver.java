package com.sunkaisens.gisandsms.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.MyApp;
import com.sunkaisens.gisandsms.event.ContactLocation;
import com.sunkaisens.gisandsms.event.GroupInfo;
import com.sunkaisens.gisandsms.event.LastMessageSMS;
import com.sunkaisens.gisandsms.event.MessageEvent;
import com.sunkaisens.gisandsms.event.MessageSMS;
import com.sunkaisens.gisandsms.event.ReceiveServerMsg;
import com.sunkaisens.gisandsms.utils.BaseUtils;
import com.sunkaisens.gisandsms.utils.ListenerHelper;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.UUID;

/**
 * @author:sun
 * @date:2018/12/28
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */

/**
 * 数据库观察者
 */
public class SmsDatabaseChaneObserver extends ContentObserver {
    // 只检查收件箱
    private static final Uri MMSSMS_ALL_MESSAGE_URI = Uri.parse("content://sms/inbox");
    private static final String SORT_FIELD_STRING = "_id asc";  // 排序
    private static final String DB_FIELD_ID = "_id";
    private static final String DB_FIELD_ADDRESS = "address";
    private static final String DB_FIELD_PERSON = "person";
    private static final String DB_FIELD_BODY = "body";
    private static final String DB_FIELD_DATE = "date";
    private static final String DB_FIELD_TYPE = "type";
    private static final String DB_FIELD_THREAD_ID = "thread_id";
    private static final String[] ALL_DB_FIELD_NAME = {
            DB_FIELD_ID, DB_FIELD_ADDRESS, DB_FIELD_PERSON, DB_FIELD_BODY,
            DB_FIELD_DATE, DB_FIELD_TYPE, DB_FIELD_THREAD_ID};
    private static int mMessageCount = -1;

    private static final long DELTA_TIME = 60 * 1000;
    private ContentResolver mResolver;

    public SmsDatabaseChaneObserver(ContentResolver resolver, Handler handler) {
        super(handler);
        mResolver = resolver;
    }

    @Override
    public void onChange(boolean selfChange) {
        onReceiveSms();
        Log.d("sjy", "content onchange ");
    }

    private void onReceiveSms() {
        Cursor cursor = null;
        // 添加异常捕捉
        try {
            cursor = mResolver.query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
                    null, null, SORT_FIELD_STRING);
            final int count = cursor.getCount();
            if (count <= mMessageCount) {
                mMessageCount = count;
                return;
            }
            // 发现收件箱的短信总数目比之前大就认为是刚接收到新短信---如果出现意外，请神保佑
            // 同时认为id最大的那条记录为刚刚新加入的短信的id---这个大多数是这样的，发现不一样的情况的时候可能也要求神保佑了
            mMessageCount = count;
            if (cursor != null) {
                cursor.moveToLast();
                long smsdate = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE)));
                long nowdate = System.currentTimeMillis();
                // 如果当前时间和短信时间间隔超过60秒,认为这条短信无效
                if (nowdate - smsdate > DELTA_TIME) {
                    return;
                }
                String strAddress = cursor.getString(cursor.getColumnIndex(DB_FIELD_ADDRESS));    // 短信号码
                String strbody = cursor.getString(cursor.getColumnIndex(DB_FIELD_BODY));          // 在这里获取短信信息
                int smsid = cursor.getInt(cursor.getColumnIndex(DB_FIELD_ID));
                if (!TextUtils.isEmpty(strAddress) && !TextUtils.isEmpty(strbody)) {

                    Log.d("sjy", "get sms from database sms is : " + strbody);
                    Log.d("sjy", "get sms from database number is : " + strAddress);
                    Log.d("sjy", "get sms from database smsid is : " + smsid);


                    try {
                        ReceiveServerMsg msg = JSON.parseObject(strbody, ReceiveServerMsg.class);

                        Log.d("sjy", "parse json to bean :" + msg.toString());

                        switch (msg.getType()) {
                            //收到的gis消息
                            case GlobalVar.SEND_MSG_TYPE.GIS_MSG:
                                String result = msg.getResult();
                                Log.d("sjy", "get gis data :" + result);
                                ContactLocation contactLocation = JSON.parseObject(result, ContactLocation.class);
                                EventBus.getDefault().post(contactLocation);
                                break;
                            //收到的群组好友列表
                            case GlobalVar.SEND_MSG_TYPE.GIS_GROUP_MSG:

                                GroupInfo groupInfo = JSON.parseObject(msg.getResult(), GroupInfo.class);
                                Log.d("sjy", "set contact list :" + groupInfo.getUri().size());
                                GlobalVar.getGlobalVar().setContactList(groupInfo.getUri());
                                Log.d("sjy", "set group no :" + groupInfo.getGroupNo());
                                GlobalVar.getGlobalVar().setGroupNo(groupInfo.getGroupNo());
                                break;

                            //群组消息
                            case GlobalVar.SEND_MSG_TYPE.NORMAL_MSG:

                                break;
                            //全部好友的gis位置信息
                            case GlobalVar.SEND_MSG_TYPE.GROUP_MSG:
                                List<ContactLocation> contactLocations = JSON.parseArray(strbody, ContactLocation.class);
                                EventBus.getDefault().post(contactLocations);
                                break;
                            default:

                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("sjy", "parse has exception ");
                        //获取到短信的数据
                        LastMessageSMS lastMessageSMS = new LastMessageSMS();
                        lastMessageSMS.setData(System.currentTimeMillis());
                        lastMessageSMS.setLastSMS(strbody);
                        lastMessageSMS.setRemoteNumber(strAddress);
                        String localNumber = BaseUtils.getInstance().getLocalNumber(MyApp.getContext());
                        List<LastMessageSMS> list = DataSupport.where("localNumber = ? and remoteNumber = ?", localNumber, strAddress).find(LastMessageSMS.class);
                        //更新
                        if (list != null && list.size() != 0) {
                            lastMessageSMS.updateAll("localNumber = ? and remoteNumber = ?", localNumber, strAddress);
                            ListenerHelper.executeOnMessageUpdate(lastMessageSMS);
                            //新存
                        } else {
                            lastMessageSMS.setRemoteNumber(strAddress);
                            lastMessageSMS.setLocalNumber(BaseUtils.getInstance().getLocalNumber(MyApp.getContext()));
                            lastMessageSMS.save();
                            ListenerHelper.executeOnReceiveNewMessageSms(lastMessageSMS);
                        }
                        saveSms(strAddress, strbody, MyApp.getContext());
                        //收到数据之后,先本地存,然后发送通知
                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.setContent(strbody);
                        messageEvent.setNumber(strAddress);
                        EventBus.getDefault().post(messageEvent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {  // 有可能cursor都没有创建成功
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
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
    private void saveSms(String num, String con, Context context) {
        MessageSMS messageSMS = new MessageSMS();
        messageSMS.setIsRead(1);
        messageSMS.setMsgType(GlobalVar.IN_TEXT_MESSAGE);
        messageSMS.setLocalAccount(BaseUtils.getInstance().getLocalNumber(context));
        messageSMS.setLocalMsgID(UUID.randomUUID().toString());
        messageSMS.setRemoteAccount(num);
        messageSMS.setStartTime(System.currentTimeMillis());
        messageSMS.setMsg(con);
        messageSMS.save();
        EventBus.getDefault().post(messageSMS);
    }
}