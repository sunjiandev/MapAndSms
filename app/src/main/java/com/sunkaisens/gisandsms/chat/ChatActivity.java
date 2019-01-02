package com.sunkaisens.gisandsms.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseActivity;
import com.sunkaisens.gisandsms.chat.contract.ChatContract;
import com.sunkaisens.gisandsms.chat.presenter.ChatPresenter;
import com.sunkaisens.gisandsms.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ChatActivity extends BaseActivity implements ChatContract.View {
    @BindView(R.id.chat_toolbar)
    Toolbar chatToolbar;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;
    @BindView(R.id.et_message_input)
    EditText etMessageInput;
    @BindView(R.id.btu_send_content)
    Button btuSendContent;
    private String remoteNumber;
    private ChatPresenter presenter;

    @Override
    protected void initView() {

        setSupportActionBar(chatToolbar);
        chatToolbar.setNavigationIcon(R.mipmap.back);

        remoteNumber = getIntent().getStringExtra(GlobalVar.INTENT_DATA);

        presenter = new ChatPresenter(this, remoteNumber);

        presenter.initAdapter(recyclerViewChat);

        chatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chatToolbar.setTitle(remoteNumber);


    }

    @Override
    protected int initLayout() {
        return R.layout.activity_chat;
    }


    @OnClick(R.id.btu_send_content)
    public void onViewClicked() {
        String sms = etMessageInput.getText().toString().trim();
        if (!TextUtils.isEmpty(sms)) {
            presenter.sendSms(sms);
            etMessageInput.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call:
                call(remoteNumber);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.showToast(this, "拨号失败,请到设置界面打开拨打电话的权限");
            return;
        } else {
            startActivity(intent);
        }
    }

    @Override
    public View getChatView(int viewId) {
        return findViewById(viewId);
    }
}
