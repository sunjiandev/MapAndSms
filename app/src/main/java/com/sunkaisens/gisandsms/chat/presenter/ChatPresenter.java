package com.sunkaisens.gisandsms.chat.presenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.chat.ChatActivity;
import com.sunkaisens.gisandsms.chat.ChatAdapter;
import com.sunkaisens.gisandsms.chat.contract.ChatContract;
import com.sunkaisens.gisandsms.chat.contract.ChatContract.Presenter;
import com.sunkaisens.gisandsms.event.GroupSms;
import com.sunkaisens.gisandsms.event.LastMessageSMS;
import com.sunkaisens.gisandsms.event.MessageSMS;
import com.sunkaisens.gisandsms.sms.SMSMethod;
import com.sunkaisens.gisandsms.utils.BaseUtils;
import com.sunkaisens.gisandsms.utils.ListenerHelper;
import com.sunkaisens.gisandsms.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ChatPresenter implements Presenter {

    private ChatActivity view;
    private String remoteNumber;
    private ChatAdapter adapter;
    private List<MessageSMS> messageSMS;
    private RecyclerView mRecyclerView;
    private boolean group;

    public ChatPresenter(ChatContract.View view, String remoteNumber) {
        this.view = (ChatActivity) view;
        this.remoteNumber = remoteNumber;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


    }

    @Override
    public List<MessageSMS> getMessageSmsList() {
        Log.d("sjy", "get message sms list is group :" + group);
        if (group) {

            Log.d("sjy", "get message sms list by group number :" + remoteNumber);
            messageSMS = DataSupport.where("groupNumber = ?", remoteNumber).find(MessageSMS.class);
        } else {
            Log.d("sjy", "get message sms list by contact number  and remote number :" + remoteNumber);
            messageSMS = DataSupport.where("localAccount = ? and remoteAccount = ? and isgroup = ?", BaseUtils.getInstance().getLocalNumber(), remoteNumber, "0").find(MessageSMS.class);
        }
        return messageSMS;
    }

    @Override
    public void sendSms(String sms, boolean group) {


        MessageSMS messageSMS = new MessageSMS();
        messageSMS.setMsg(sms);
        messageSMS.setStartTime(System.currentTimeMillis());
        messageSMS.setRemoteAccount(remoteNumber);
        messageSMS.setLocalAccount(BaseUtils.getInstance().getLocalNumber());
        messageSMS.setIsRead(0);
        messageSMS.setMsgType(GlobalVar.TO_TEXT_MESSAGE);
        messageSMS.setLocalMsgID(UUID.randomUUID().toString());
        messageSMS.setGroup(group);
        if (group) {
            //设置组号
            messageSMS.setGroupNumber(remoteNumber);
        }
        messageSMS.save();
        adapter.insert(messageSMS);

        int itemCount = adapter.getItemCount();
        Log.d("sjy", "item size :" + itemCount);
        mRecyclerView.smoothScrollToPosition(itemCount);
        //更新历史记录

        LastMessageSMS lastMessageSMS = new LastMessageSMS();
        lastMessageSMS.setLastSMS(sms);
        lastMessageSMS.setLocalNumber(BaseUtils.getInstance().getLocalNumber());
        lastMessageSMS.setRemoteNumber(remoteNumber);
        lastMessageSMS.setGroup(group);

        lastMessageSMS.updateAll("localNumber = ? and remoteNumber = ?", BaseUtils.getInstance().getLocalNumber(), remoteNumber);

        List<LastMessageSMS> smsList = DataSupport.where("remotenumber = ?", remoteNumber).find(LastMessageSMS.class);
        if (smsList != null && smsList.size() != 0) {
            //通知刷新
            ListenerHelper.executeOnMessageUpdate(lastMessageSMS);
        } else {
            lastMessageSMS.save();
            ListenerHelper.executeOnReceiveNewMessageSms(lastMessageSMS);
        }
        if (group) {
            Map<String, Object> map = new HashMap<>(5);
            GroupSms groupSms = new GroupSms();
            groupSms.setN(remoteNumber);
            groupSms.setC(sms);
            map.put("b", groupSms);
            map.put("t", GlobalVar.SEND_MSG_TYPE.NORMAL_MSG);
            map.put("u", BaseUtils.getInstance().getLocalNumber());
            map.put("m", "");
            map.put("r", "");

            String jsonGroupSms = JSON.toJSONString(map);
            Log.d("sjy", "send group sms content is:" + jsonGroupSms);

            SMSMethod.getInstance(view).SendMessage(GlobalVar.SMS_CENTER_NUMBER, jsonGroupSms);

        } else {
            //发送短信

            SMSMethod.getInstance(view).SendMessage(remoteNumber, sms);
        }
    }

    @Override
    public void callPhone(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(view, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.showToast(view, "拨号失败,请到设置界面打开拨打电话的权限");
            return;
        } else {
            view.startActivity(intent);
        }
    }


    @Override
    public void initAdapter(RecyclerView recyclerView, boolean group) {
        this.mRecyclerView = recyclerView;
        this.group = group;
        LinearLayoutManager manager = new LinearLayoutManager(view);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new ChatAdapter(view, getMessageSmsList(), 0);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    /**
     * 接收到sms
     *
     * @param sms
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSms(MessageSMS sms) {
        Log.d("sjy", "receive sms :" + sms.toString());
        Log.d("sjy", "is group sms :" + group);
        Log.d("sjy", "remote number  is :" + remoteNumber);
        Log.d("sjy", "group number is :" + sms.getGroupNumber());

        if (group) {
            if (adapter != null && sms.getGroupNumber().equals(remoteNumber)) {
                adapter.insert(sms);
            }
        } else {
            if (adapter != null && sms.getRemoteAccount().equals(remoteNumber) && TextUtils.isEmpty(sms.getGroupNumber())) {
                adapter.insert(sms);
            }
        }

    }


}
