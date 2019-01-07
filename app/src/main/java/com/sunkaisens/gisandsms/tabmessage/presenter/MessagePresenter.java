package com.sunkaisens.gisandsms.tabmessage.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseRecyclerAdapter;
import com.sunkaisens.gisandsms.chat.ChatActivity;
import com.sunkaisens.gisandsms.event.LastMessageSMS;
import com.sunkaisens.gisandsms.tabmessage.LastMessageSmsAdapter;
import com.sunkaisens.gisandsms.tabmessage.MessageFragment;
import com.sunkaisens.gisandsms.tabmessage.contract.MessageContract;
import com.sunkaisens.gisandsms.utils.ListenerHelper;

import org.litepal.crud.DataSupport;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MessagePresenter implements MessageContract.Presenter, ListenerHelper.OnLastMessageUpdateCallBack {


    private MessageFragment view;
    private final Context context;
    private LastMessageSmsAdapter adapter;
    private List<LastMessageSMS> lastMessageSMS;

    public MessagePresenter(MessageContract.View view) {
        this.view = (MessageFragment) view;
        context = ((MessageFragment) view).getContext();
    }

    @Override
    public List<LastMessageSMS> getLastMessageSms() {

        lastMessageSMS = DataSupport.findAll(LastMessageSMS.class);

        ListenerHelper.setOnLastMessageUpdateCallBack(this);
        return lastMessageSMS;
    }

    @Override
    public void initAdapter(RecyclerView recyclerView) {

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new LastMessageSmsAdapter(context, getLastMessageSms(), R.layout.fragment_message_layout_item);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                LastMessageSMS lastMessageSMS = MessagePresenter.this.lastMessageSMS.get(position);
                String number = lastMessageSMS.getRemoteNumber();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(GlobalVar.INTENT_DATA, number);
                intent.putExtra(GlobalVar.INTENT_GROUP, lastMessageSMS.isGroup());
                Log.d("sjy", "is group sms :" + lastMessageSMS.isGroup());

                context.startActivity(intent);

            }
        });


    }

    @Override
    public void reflashData() {

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 消息更新
     *
     * @param messageSMS 消息
     */
    @Override
    public void onMessageUpdate(final LastMessageSMS messageSMS) {

        Observable.from(lastMessageSMS).filter(new Func1<LastMessageSMS, Boolean>() {
            @Override
            public Boolean call(LastMessageSMS sms) {
                return sms.getRemoteNumber().equals(messageSMS.getRemoteNumber());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<LastMessageSMS>() {
            @Override
            public void call(LastMessageSMS sms) {
                sms.setLastSMS(messageSMS.getLastSMS());
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 插入消息
     *
     * @param sms 消息
     */
    @Override
    public void onReceiveNewMessageSms(LastMessageSMS sms) {

        if (adapter != null) {
            adapter.insert(sms, 0);
        }
    }
}
