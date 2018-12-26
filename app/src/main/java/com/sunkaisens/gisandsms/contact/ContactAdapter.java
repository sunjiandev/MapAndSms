package com.sunkaisens.gisandsms.contact;

import android.content.Context;

import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseRecyclerAdapter;
import com.sunkaisens.gisandsms.base.BaseRecyclerHolder;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/24
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ContactAdapter extends BaseRecyclerAdapter<String> {

    private Context context;
    private List<String> list;

    public ContactAdapter(Context context, List<String> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling, List payloads) {
        holder.setText(R.id.user_account, list.get(position));

    }
}
