package com.sunkaisens.gisandsms.tabcontact;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author:sun
 * @date:2019/1/2
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MyGroupActivity extends BaseActivity {
    @BindView(R.id.group_number)
    TextView groupNumber;
    @BindView(R.id.user_layout)
    RelativeLayout userLayout;

    @Override
    protected void initView() {

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_my_group;
    }

    @OnClick(R.id.user_layout)
    public void onViewClicked() {


    }
}
