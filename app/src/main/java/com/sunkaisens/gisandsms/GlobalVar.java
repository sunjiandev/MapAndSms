package com.sunkaisens.gisandsms;

import com.sunkaisens.gisandsms.event.LastMessageSMS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:sun
 * @date:2018/12/24
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class GlobalVar {

    private static GlobalVar globalVar;

    private GlobalVar() {
    }

    public static GlobalVar getGlobalVar() {
        if (globalVar == null) {
            synchronized (GlobalVar.class) {
                if (globalVar == null) {
                    globalVar = new GlobalVar();
                }
            }
        }
        return globalVar;
    }
    /**
     * 发出去的文本消息
     */
    public static final int TO_TEXT_MESSAGE = 1;
    /**
     * 发出去的图片消息
     */
    public static final int TO_IMAGE_MESSAGE = 2;
    /**
     * 发出去的即时语音消息
     */
    public static final int TO_AUDIO_MESSAGE = 3;
    /**
     *
     */
    public static final int TO_VIDEO_MESSAGE = 4;
    /**
     *
     */
    public static final int TO_FILE_MESSAGE = 5;

    /**
     * 收到的文本消息
     */
    public static final int IN_TEXT_MESSAGE = 6;
    /**
     * 收到的图片消息
     */
    public static final int IN_IMAGE_MESSAGE = 7;
    /**
     * 收到的即时语音消息
     */
    public static final int IN_AUDIO_MESSAGE = 8;
    /**
     * 收到的即时视频消息
     */
    public static final int IN_VIDEO_MESSAGE = 9;
    /**
     * 收到的文件消息
     */
    public static final int IN_FILE_MESSAGE = 10;

    /**
     * intent 传递数据的key
     */
    public static final String INTENT_DATA = "intent_data";
    /**
     * 标记的网络的状态
     */
    public static boolean NETWORKISCONNNECT = true;

    /**
     * 保存的上报位置的距离
     */
    public static final String POST_DISTANCE = "POST_DISTANCE";

    /**
     * 上报位置的时间间隔
     */
    public static final String UPLOAD_TIME = "UPLOAD_TIME";
    /**
     * 是否开启高德定位
     */
    public static final String LOCATION = "LOCATION";

    /**
     * 保存联系人的集合
     */

    private List<String> contactLists = new ArrayList<>();


    /**
     * 地图配置信息的类型,上班位置的距离
     */

    public static final int TYPE_POST_DISTANCE = 771;
    /**
     * 上报位置的时间间隔
     */
    public static final int TYPE_UPLOAD_TIME = 454;



    public static final String REQUEST_API = "/api/user_coordinateapi";


    /**
     * 短信中心的号码
     */

    public static final String SMS_CENTER_NUMBER = "15320011990";
    /**
     * 保存最后一条聊天记录的集合
     */
    private List<LastMessageSMS> lastMessageSMSList = new ArrayList<>();

    /**
     * 获取最后一条记录的数据
     *
     * @return 数据
     */
    public List<LastMessageSMS> getLastMessageSMSList() {
        return lastMessageSMSList;
    }

    /**
     * 插入一条数据
     *
     * @param sms 数据
     */
    public void insertLastMessageSms(LastMessageSMS sms) {
        lastMessageSMSList.add(0, sms);
    }

    /**
     * 插入一个集合的数据
     *
     * @param smsList 集合
     */
    public void insertLastMessageSmsList(List<LastMessageSMS> smsList) {
        lastMessageSMSList.clear();
        lastMessageSMSList.addAll(smsList);
    }


    /**
     * 获取联系人的集合
     *
     * @return 联系人集合
     */
    public List<String> getContactLists() {
        return contactLists;
    }

    /**
     * 设置用户数据
     *
     * @param contacts 用户数据集合
     */
    public void setContactList(List<String> contacts) {
        contactLists.clear();
        contacts.addAll(contacts);
    }


}
