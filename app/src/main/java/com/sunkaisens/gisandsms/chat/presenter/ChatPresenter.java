package com.sunkaisens.gisandsms.chat.presenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.chat.ChatActivity;
import com.sunkaisens.gisandsms.chat.ChatAdapter;
import com.sunkaisens.gisandsms.chat.contract.ChatContract;
import com.sunkaisens.gisandsms.chat.contract.ChatContract.Presenter;
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

import java.util.List;
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
        if (group) {
            messageSMS = DataSupport.where("groupNumber = ?", remoteNumber).find(MessageSMS.class);
        } else {
            messageSMS = DataSupport.where("localAccount = ? and remoteAccount = ?", BaseUtils.getInstance().getLocalNumber(view), remoteNumber).find(MessageSMS.class);
        }
        return messageSMS;
    }

    @Override
    public void sendSms(String sms, boolean group) {


        MessageSMS messageSMS = new MessageSMS();
        messageSMS.setMsg(sms);
        messageSMS.setStartTime(System.currentTimeMillis());
        messageSMS.setRemoteAccount(remoteNumber);
        messageSMS.setLocalAccount(BaseUtils.getInstance().getLocalNumber(view));
        messageSMS.setIsRead(0);
        messageSMS.setMsgType(GlobalVar.TO_TEXT_MESSAGE);
        messageSMS.setLocalMsgID(UUID.randomUUID().toString());
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
        lastMessageSMS.setLocalNumber(BaseUtils.getInstance().getLocalNumber(view));
        lastMessageSMS.setRemoteNumber(remoteNumber);

        lastMessageSMS.updateAll("localNumber = ? and remoteNumber = ?", BaseUtils.getInstance().getLocalNumber(view), remoteNumber);

        List<LastMessageSMS> smsList = DataSupport.where("remotenumber = ?", remoteNumber).find(LastMessageSMS.class);
        if (smsList!=null&&smsList.size()!=0){
            //通知刷新
            ListenerHelper.executeOnMessageUpdate(lastMessageSMS);
        }else{
            lastMessageSMS.save();
            ListenerHelper.executeOnReceiveNewMessageSms(lastMessageSMS);
        }

        //发送短信
        SMSMethod.getInstance(view).SendMessage(remoteNumber, sms);
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
        if (adapter != null && sms.getRemoteAccount().equals(remoteNumber)) {
            adapter.insert(sms);
        }
    }


}
