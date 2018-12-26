package com.sunkaisens.gisandsms;

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
