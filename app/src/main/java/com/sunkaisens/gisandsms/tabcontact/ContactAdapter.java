package com.sunkaisens.gisandsms.tabcontact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunkaisens.gisandsms.R;

import java.util.List;

/**
 * @author:sun
 * @date:2018/12/24
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

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
    private OnItemClickListener listener;

    public ContactAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
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
        return list.size() + 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder holder = null;

        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case NOEMAL_LAYOUT:
                holder = new ViewHolder(inflater.inflate(R.layout.fragment_contact_layout_item, parent, false));
                break;
            case HEADER_LAYOUT:
                holder = new ViewHolder(inflater.inflate(R.layout.fragment_contact_layout_item_header, parent, false));
                break;
            default:
                break;

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position > 0) {
            String number = list.get(position - 1);
            holder.userAccount.setText(number);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userAccount;

        public ViewHolder(View itemView) {
            super(itemView);
            userAccount = itemView.findViewById(R.id.user_account);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    /**
     * 插入数据
     *
     * @param contact  数据
     * @param position 插入位置
     */
    public void insert(String contact, int position) {
        list.add(position, contact);
        notifyItemChanged(position);
    }

    /**
     * 出入数据
     *
     * @param contacts 数据
     */
    public void insert(List<String> contacts) {
        list.addAll(contacts);
        notifyDataSetChanged();
    }
}
