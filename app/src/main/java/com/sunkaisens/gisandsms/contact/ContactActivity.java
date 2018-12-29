package com.sunkaisens.gisandsms.contact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseActivity;
import com.sunkaisens.gisandsms.base.BaseRecyclerAdapter;
import com.sunkaisens.gisandsms.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;

/**
 * @author:sjy
 * @email:sunjianyun@sunkaisens.com
 * @data:2018/4/9 10:20
 */

public class ContactActivity extends BaseActivity {
    public static final String TAG = ContactActivity.class.getCanonicalName();
    public static final int REQUEST_CALL_PHONE_PERMISSION = 540;
    @BindView(R.id.contact_list)
    RecyclerView contactList;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.no_contact_layout)
    RelativeLayout noContactLayout;

    private String number;
    private List<String> contactLists;


    @Override
    protected void initView() {

        setSupportActionBar(toolbar);

        initContact();

        //有联系人数据
        if (contactLists != null && contactLists.size() != 0) {
            ContactAdapter adapter = new ContactAdapter(this, contactLists, R.layout.fragment_contact_layout_item);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            contactList.setLayoutManager(linearLayoutManager);
            contactList.setAdapter(adapter);

            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(RecyclerView parent, View view, int position) {
                    number = contactLists.get(position);
                    showSelectDialog(number);
                }
            });

            //没有联系人数据¬
        } else {

            noContactLayout.setVisibility(View.VISIBLE);

        }


    }

    private void showSelectDialog(final String number) {

        new AlertDialog.Builder(this).setTitle("选择业务").setItems(new String[]{"发送短消息", "拨打电话"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    //发送短信
                    case 0:
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                        sendIntent.setData(Uri.parse("smsto:" + number));
                        sendIntent.putExtra("sms_body", "");
                        ContactActivity.this.startActivity(sendIntent);
                        break;
                    //拨打电话
                    case 1:
                        if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("sjy", "未授权");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                            }
                            return;
                        } else {
                            call(number);
                        }


                        break;
                    default:
                        break;
                }
            }
        }).create().show();

    }

    @SuppressLint("MissingPermission")
    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        ContactActivity.this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:

                go(SettingActivity.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected int initLayout() {
        return R.layout.activity_contact;
    }

    private void initContact() {

        contactLists = GlobalVar.getGlobalVar().getContactLists();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PHONE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(number);
                } else {
                    ToastUtils.showToast(this, "拨打电话权限拒绝,拨号失败");
                }
                break;
            default:
                break;
        }
    }
}
