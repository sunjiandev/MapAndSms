package com.sunkaisens.gisandsms.sms;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.MyApp;
import com.sunkaisens.gisandsms.event.ContactLocation;
import com.sunkaisens.gisandsms.event.GroupInfo;
import com.sunkaisens.gisandsms.event.LastMessageSMS;
import com.sunkaisens.gisandsms.event.MessageEvent;
import com.sunkaisens.gisandsms.event.MessageSMS;
import com.sunkaisens.gisandsms.event.ReceiveServerMsg;
import com.sunkaisens.gisandsms.event.ServerInfo;
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
    private List<LastMessageSMS> list;

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
                            //获取多个位置信息
                            case GlobalVar.SEND_MSG_TYPE.GIS_MSG:

                                List<ContactLocation> contactLocations = JSON.parseArray(msg.getResult(), ContactLocation.class);

                                EventBus.getDefault().post(contactLocations);
                                break;
                            //获取单个位置
                            case GlobalVar.SEND_MSG_TYPE.GIS_GROUP_MSG:
                                String result = msg.getResult();
                                Log.d("sjy", "get gis data :" + result);
                                ContactLocation contactLocation = JSON.parseObject(result, ContactLocation.class);
                                EventBus.getDefault().post(contactLocation);
                                break;

                            //群组消息
                            case GlobalVar.SEND_MSG_TYPE.NORMAL_MSG:

                                String groupSmsBody = msg.getResult();
                                Log.d("sjy", "receive group sms body :" + groupSmsBody);

                                saveSms(strAddress, groupSmsBody, true);
                                saveLastSMS(strAddress, groupSmsBody, true);

                                //收到数据之后,先本地存,然后发送通知
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setContent(strbody);
                                messageEvent.setNumber(strAddress);
                                EventBus.getDefault().post(messageEvent);

                                break;
                            //获取群组组号还有组成员
                            case GlobalVar.SEND_MSG_TYPE.GROUP_MSG:
                                GroupInfo groupInfo = JSON.parseObject(msg.getResult(), GroupInfo.class);

                                Log.d("sjy", "set contact list :" + groupInfo.getUri().size());
                                GlobalVar.getGlobalVar().setContactList(groupInfo.getUri());
                                Log.d("sjy", "set group no :" + groupInfo.getGroupNo());
                                GlobalVar.getGlobalVar().setGroupNo(groupInfo.getGroupNo());
                                //获取自己的号码
                                break;
                            case GlobalVar.SEND_MSG_TYPE.REQUEST_LOCAL_NUMBER:
                                String localNumber = msg.getResult();
                                Log.d("sjy", "get my local number :" + localNumber);
                                BaseUtils.getInstance().setLocalNumber(localNumber);


                                //获取完自己的号码之后 获取群组组号还有组成员
                                ServerInfo info = new ServerInfo();
                                info.setU(BaseUtils.getInstance().getLocalNumber());
                                info.setB("");
                                info.setM("GET");
                                info.setR(GlobalVar.REQUEST_API_GROUP);
                                info.setT(GlobalVar.SEND_MSG_TYPE.GROUP_MSG);

                                String strJson = JSON.toJSONString(info);

                                Log.d("sjy", "json str :" + strJson);
                                Log.d("sjy", "convert contact str :" + strJson);
                                SMSMethod.getInstance(MyApp.getContext()).SendMessage(GlobalVar.SMS_CENTER_NUMBER, strJson);


                                //获取完之后拉所有人的实时位置
                                ServerInfo serverInfo = new ServerInfo();
                                serverInfo.setT(GlobalVar.SEND_MSG_TYPE.GIS_MSG);
                                serverInfo.setM("GET");
                                serverInfo.setR(GlobalVar.REQUEST_API);
                                serverInfo.setU(BaseUtils.getInstance().getLocalNumber());
                                serverInfo.setB("");
                                String jsonString = JSON.toJSONString(serverInfo);

                                Log.d("sjy", "convert gis str :" + jsonString);

                                SMSMethod.getInstance(MyApp.getContext()).SendMessage(GlobalVar.SMS_CENTER_NUMBER, jsonString);


                                break;
                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("sjy", "parse has exception ");
                        if (strAddress.equals(GlobalVar.SMS_CENTER_NUMBER)) {
                            Log.d("sjy", "sms is err ,don`t save");
                        } else {
                            saveLastSMS(strAddress, strbody, false);
                        }
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

    private void saveLastSMS(String strAddress, String strbody, boolean isGroup) {
        String localNumber = BaseUtils.getInstance().getLocalNumber();
        //获取到短信的数据
        LastMessageSMS lastMessageSMS = new LastMessageSMS();
        lastMessageSMS.setData(System.currentTimeMillis());
        if (isGroup) {
            JSONObject jsonObject = JSON.parseObject(strbody);
            strAddress = jsonObject.getString("n");
            strbody = jsonObject.getString("b");
            list = DataSupport.where("remoteNumber = ?", strAddress).find(LastMessageSMS.class);
        } else {
            list = DataSupport.where("localNumber = ? and remoteNumber = ?", localNumber, strAddress).find(LastMessageSMS.class);
        }
        Log.d("sjy", "get contact sms list :" + list.size());
        lastMessageSMS.setLastSMS(strbody);
        lastMessageSMS.setGroup(isGroup);
        lastMessageSMS.setRemoteNumber(strAddress);
        //更新
        if (list != null && list.size() != 0) {
            if (isGroup) {
                lastMessageSMS.updateAll("remoteNumber = ?", strAddress);

            } else {
                lastMessageSMS.updateAll("localNumber = ? and remoteNumber = ?", localNumber, strAddress);
            }
            Log.d("sjy", "update contact last sms ");

            ListenerHelper.executeOnMessageUpdate(lastMessageSMS);
            //新存
        } else {
            lastMessageSMS.setRemoteNumber(strAddress);
            lastMessageSMS.setLocalNumber(BaseUtils.getInstance().getLocalNumber());
            lastMessageSMS.save();
            Log.d("sjy", "save last sms :" + lastMessageSMS.toString());
            ListenerHelper.executeOnReceiveNewMessageSms(lastMessageSMS);
        }
        if (!isGroup) {
            saveSms(strAddress, strbody, false);
        }
        //收到数据之后,先本地存,然后发送通知
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setContent(strbody);
        messageEvent.setNumber(strAddress);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 保存聊天内容
     *
     * @param num 对端号码,如果是单聊的话,如果是组聊的话是短信中心的号码
     * @param con
     */
    private void saveSms(String num, String con, boolean isgroup) {
        MessageSMS messageSMS = new MessageSMS();
        messageSMS.setGroup(isgroup);
        if (isgroup) {
            JSONObject jsonObject = JSON.parseObject(con);
            //获取组号
            String groupNo = jsonObject.getString("n");
            //发送端的号码
            String remoteNumber = jsonObject.getString("u");
            //消息内容
            String content = jsonObject.getString("b");

            Log.d("sjy", "get group sms body :" + con);

            Log.d("sjy", "get group number from json result :" + groupNo);
            Log.d("sjy", "get remote number from json result :" + remoteNumber);
            Log.d("sjy", "get body number from json result :" + content);

            messageSMS.setGroupNumber(groupNo);
            messageSMS.setRemoteAccount(remoteNumber);
            messageSMS.setMsg(content);
        } else {
            messageSMS.setGroupNumber("");
            messageSMS.setRemoteAccount(num);
            messageSMS.setMsg(con);
        }
        messageSMS.setIsRead(1);
        messageSMS.setMsgType(GlobalVar.IN_TEXT_MESSAGE);
        messageSMS.setLocalAccount(BaseUtils.getInstance().getLocalNumber());
        messageSMS.setLocalMsgID(UUID.randomUUID().toString());
        messageSMS.setStartTime(System.currentTimeMillis());
        messageSMS.save();
        EventBus.getDefault().post(messageSMS);
    }
}