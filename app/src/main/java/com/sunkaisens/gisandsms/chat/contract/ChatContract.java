package com.sunkaisens.gisandsms.chat.contract;

import android.support.v7.widget.RecyclerView;

import com.sunkaisens.gisandsms.event.MessageSMS;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public interface ChatContract {
    interface Model {
    }

    interface View {

        android.view.View getChatView(int viewId);
    }

    interface Presenter {
        /**
         * 初始化适配器
         *
         * @param recyclerView 列表
         */
        void initAdapter(RecyclerView recyclerView);

        /**
         * 获取聊天数据
         *
         * @return 数据
         */
        List<MessageSMS> getMessageSmsList();

        /**
         * 发送短信
         *
         * @param sms 短信内容
         */
        void sendSms(String sms);

        /**
         * 拨打电话
         *
         * @param number
         */
        void callPhone(String number);
    }
}
