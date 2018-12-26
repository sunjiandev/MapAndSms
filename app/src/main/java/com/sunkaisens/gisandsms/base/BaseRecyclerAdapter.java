package com.sunkaisens.gisandsms.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @author sjy
 * 时间  2018/6/6
 * 邮箱  sjy_mail@163.com
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    /**
     * 上下文
     */
    private Context context;
    /**
     * 数据源
     */
    private List<T> list;
    /**
     * 布局器
     */
    private LayoutInflater inflater;
    /**
     * 布局id
     */
    private int itemLayoutId;
    /**
     * 是否在滚动
     */
    private boolean isScrolling;
    /**
     * 点击事件监听器
     */
    private OnItemClickListener listener;
    //长按监听器
    private OnItemLongClickListener longClickListener;
    private RecyclerView recyclerView;
    private int viewId;

    /**
     * 在RecyclerView提供数据的时候调用
     *
     * @param recyclerView rv
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    /**
     * 定义一个点击事件接口回调
     */
    public interface OnItemClickListener {
        /**
         * 点击事件
         *
         * @param parent
         * @param view
         * @param position
         */
        void onItemClick(RecyclerView parent, View view, int position);
    }

    /**
     * 设置点击事件监听
     *
     * @param onItemClickListener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }


    public interface OnItemLongClickListener {
        /**
         * 条目的点击事件
         *
         * @param parent
         * @param view
         * @param position
         * @return
         */
        boolean onItemLongClick(RecyclerView parent, View view, int position);
    }

    /**
     * 设置长按点击事件监听
     *
     * @param onItemLongClickListener 监听器
     */
    public void setItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.longClickListener = onItemLongClickListener;
    }

    /**
     * 插入一项
     *
     * @param item
     * @param position
     */
    public void insert(T item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void insert(T item) {
        list.add(item);
        notifyDataSetChanged();
    }

    public void insert(int position, List<T> items) {
        list.addAll(position, items);
        notifyDataSetChanged();
    }

    public void insertData(T item, int position) {
        list.add(position, item);
        notifyDataSetChanged();
    }

    /**
     * 删除一项
     *
     * @param position 删除位置
     */
    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public BaseRecyclerAdapter(Context context, List<T> list, int itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(itemLayoutId, parent, false);
        return BaseRecyclerHolder.getRecyclerHolder(context, view);
    }


    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, int position, List payloads) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    listener.onItemClick(recyclerView, view, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    longClickListener.onItemLongClick(recyclerView, view, position);
                    return true;
                }
                return false;
            }
        });

        convert(holder, list.get(position), position, isScrolling, payloads);

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 填充RecyclerView适配器的方法，子类需要重写
     *
     * @param holder      ViewHolder
     * @param item        子项
     * @param position    位置
     * @param isScrolling 是否在滑动
     */
    public abstract void convert(BaseRecyclerHolder holder, T item, int position, boolean isScrolling, List payloads);
}