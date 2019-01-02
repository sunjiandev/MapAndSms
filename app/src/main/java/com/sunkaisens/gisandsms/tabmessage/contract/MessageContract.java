package com.sunkaisens.gisandsms.tabmessage.contract;

import android.support.v7.widget.RecyclerView;

import com.sunkaisens.gisandsms.event.LastMessageSMS;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description: mvp 父类接口
 */
public interface MessageContract {
    interface Model {
    }

    interface View {
    }

    interface Presenter {
        /**
         * 获取最后一条历史记录
         *
         * @return 数据
         */
        List<LastMessageSMS> getLastMessageSms();

        /**
         * 初始化适配器
         */
        void initAdapter(RecyclerView recyclerView);

        /**
         * 刷新数据
         */
        void reflashData();
    }
}
