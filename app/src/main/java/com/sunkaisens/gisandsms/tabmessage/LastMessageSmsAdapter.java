package com.sunkaisens.gisandsms.tabmessage;

import android.content.Context;
import android.view.View;

import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseRecyclerAdapter;
import com.sunkaisens.gisandsms.base.BaseRecyclerHolder;
import com.sunkaisens.gisandsms.event.LastMessageSMS;
import com.sunkaisens.gisandsms.event.MessageSMS;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class LastMessageSmsAdapter extends BaseRecyclerAdapter<LastMessageSMS> {

    public LastMessageSmsAdapter(Context context, List<LastMessageSMS> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, LastMessageSMS item, int position, boolean isScrolling, List payloads) {

        List<MessageSMS> sms = DataSupport.where("localaccount = ? and remoteaccount = ? and isread = ?", item.getLocalNumber(), item.getRemoteNumber(), "1").find(MessageSMS.class);
        if (sms != null && sms.size() != 0) {
            holder.getView(R.id.unread_message_tips).setVisibility(View.VISIBLE);
        }
        holder.setText(R.id.message_user_account, item.getRemoteNumber());
        holder.setText(R.id.message_content, item.getLastSMS());
    }
}
