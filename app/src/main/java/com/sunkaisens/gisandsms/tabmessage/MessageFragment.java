package com.sunkaisens.gisandsms.tabmessage;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseFragment;
import com.sunkaisens.gisandsms.tabmessage.contract.MessageContract;
import com.sunkaisens.gisandsms.tabmessage.presenter.MessagePresenter;

import butterknife.BindView;

/**
 * @author:sun
 * @date:2018/12/26
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MessageFragment extends BaseFragment implements MessageContract.View {
    @BindView(R.id.listview_message)
    RecyclerView listviewMessage;
    private MessagePresenter presenter;

    @Override
    protected void initView(View view) {
        
        presenter = new MessagePresenter(this);
        presenter.initAdapter(listviewMessage);


    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void onResume() {
        super.onResume();
       presenter.initAdapter(listviewMessage);
    }
}
