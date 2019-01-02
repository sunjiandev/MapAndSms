package com.sunkaisens.gisandsms.utils;

import com.sunkaisens.gisandsms.event.LastMessageSMS;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description: 接口工具类
 */
public class ListenerHelper {

    private static OnLastMessageUpdateCallBack messageUpdateCallBack;

    public interface OnLastMessageUpdateCallBack {
        /**
         * 消息回调通知
         * @param sms 消息内容
         */
        void onMessageUpdate(LastMessageSMS sms);

        /**
         * 插入一条新的消息
         * @param sms 消息
         */
        void onReceiveNewMessageSms(LastMessageSMS sms);
    }

    /**
     * 设置监听
     *
     * @param messageUpdateCallBack 监听
     */
    public static void setOnLastMessageUpdateCallBack(OnLastMessageUpdateCallBack messageUpdateCallBack) {
        ListenerHelper.messageUpdateCallBack = messageUpdateCallBack;
    }

    /**
     * 执行监听
     */
    public static void executeOnMessageUpdate(LastMessageSMS sms) {
        if (messageUpdateCallBack != null) {
            messageUpdateCallBack.onMessageUpdate(sms);
        }
    }
    /**
     * 执行监听
     */
    public static void executeOnReceiveNewMessageSms(LastMessageSMS sms){
        if (messageUpdateCallBack != null) {
            messageUpdateCallBack.onReceiveNewMessageSms(sms);
        }
    }
}
