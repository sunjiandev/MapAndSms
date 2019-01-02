package com.sunkaisens.gisandsms.tabcontact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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

    /**
     * 头布局
     */
    private static final int HEADER_LAYOUT = 59;
    /**
     * 普通布局
     */
    private static final int NOEMAL_LAYOUT = 204;

    private Context context;
    private List<String> list;

    public ContactAdapter(Context context, List<String> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling, List payloads) {

        if (position != 0) {
            holder.setText(R.id.user_account, list.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return HEADER_LAYOUT;
        } else {
            return NOEMAL_LAYOUT;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        BaseRecyclerHolder holder = null;

        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case NOEMAL_LAYOUT:
                holder = BaseRecyclerHolder.getRecyclerHolder(context, inflater.inflate(R.layout.fragment_contact_layout_item, parent, false));
                break;
            case HEADER_LAYOUT:
                holder = BaseRecyclerHolder.getRecyclerHolder(context, inflater.inflate(R.layout.fragment_contact_layout_item_header, parent, false));
                break;
            default:
                break;

        }
        return holder;
    }
}
