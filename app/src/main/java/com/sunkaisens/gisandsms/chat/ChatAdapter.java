package com.sunkaisens.gisandsms.chat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseRecyclerAdapter;
import com.sunkaisens.gisandsms.base.BaseRecyclerHolder;
import com.sunkaisens.gisandsms.event.MessageSMS;
import com.sunkaisens.gisandsms.utils.BaseUtils;

import org.litepal.tablemanager.Connector;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ChatAdapter extends BaseRecyclerAdapter<MessageSMS> {

    private Context context;
    private List<MessageSMS> list;

    public ChatAdapter(Context context, List<MessageSMS> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, MessageSMS item, int position, boolean isScrolling, List payloads) {

        int msgType = item.getMsgType();

        MessageSMS messageSMS = new MessageSMS();
        messageSMS.setIsRead(0);
        messageSMS.updateAll("localaccount = ? and remoteaccount =? and localmsgid = ? ", item.getLocalAccount(), item.getRemoteAccount(), item.getLocalMsgID());


        switch (msgType) {
            case GlobalVar.TO_TEXT_MESSAGE:
                holder.setText(R.id.send_msg_text_time, BaseUtils.getInstance().formatLongTime(item.getStartTime()));
                holder.setText(R.id.send_msg_text, item.getMsg());
                break;
            case GlobalVar.IN_TEXT_MESSAGE:

                String sql = "UPDATE messagesms SET isread = 0 WHERE localaccount = '" + item.getLocalAccount() + "' and remoteaccount = '" + item.getRemoteAccount() + "'";
                Connector.getDatabase().execSQL(sql);

                holder.setText(R.id.receive_msg_text_time, BaseUtils.getInstance().formatLongTime(item.getStartTime()));
                holder.setText(R.id.receive_msg_text, item.getMsg());
                Log.d("sjy", "update sms is :" + item.toString());
                Log.d("sjy", "update database sms is read");

                break;
            default:
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return list == null ? 0 : list.get(position).getMsgType();
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        BaseRecyclerHolder holder = null;
        switch (viewType) {
            case GlobalVar.TO_TEXT_MESSAGE:
                holder = BaseRecyclerHolder.getRecyclerHolder(context, View.inflate(context, R.layout.activity_chat_send_text_item, null));
                break;
            case GlobalVar.IN_TEXT_MESSAGE:
                holder = BaseRecyclerHolder.getRecyclerHolder(context, View.inflate(context, R.layout.activity_chat_receive_text_item, null));
                break;
            default:
                break;

        }
        return holder;
    }
}
