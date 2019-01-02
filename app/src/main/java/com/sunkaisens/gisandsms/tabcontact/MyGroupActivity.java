package com.sunkaisens.gisandsms.tabcontact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseActivity;
import com.sunkaisens.gisandsms.chat.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author:sun
 * @date:2019/1/2
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MyGroupActivity extends BaseActivity {

    @BindView(R.id.group_toolbar)
    Toolbar groupToolbar;
    @BindView(R.id.group_number)
    TextView groupNumber;
    @BindView(R.id.my_group_layout)
    RelativeLayout myGroupLayout;
    @BindView(R.id.user_icon)
    ImageView userIcon;
    @BindView(R.id.line)
    View line;

    @Override
    protected void initView() {
        setSupportActionBar(groupToolbar);
        groupNumber.setText(GlobalVar.getGlobalVar().getGroupNo());

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_my_group;
    }


    @OnClick(R.id.my_group_layout)
    public void onViewClicked() {
        Intent intent = new Intent(MyGroupActivity.this, ChatActivity.class);
        intent.putExtra(GlobalVar.INTENT_DATA, GlobalVar.getGlobalVar().getGroupNo());
        intent.putExtra(GlobalVar.INTENT_GROUP, true);
        startActivity(intent);
    }
}
