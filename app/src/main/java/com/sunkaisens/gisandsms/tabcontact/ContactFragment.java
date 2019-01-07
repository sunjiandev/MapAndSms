package com.sunkaisens.gisandsms.tabcontact;

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
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.sunkaisens.gisandsms.GlobalVar;
import com.sunkaisens.gisandsms.R;
import com.sunkaisens.gisandsms.base.BaseFragment;
import com.sunkaisens.gisandsms.chat.ChatActivity;
import com.sunkaisens.gisandsms.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * @author:sun
 * @date:2018/12/26
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class ContactFragment extends BaseFragment {
    public static final String TAG = ContactFragment.class.getCanonicalName();
    public static final int REQUEST_CALL_PHONE_PERMISSION = 540;
    @BindView(R.id.contact_list)
    RecyclerView contactList;
    @BindView(R.id.no_contact_layout)
    RelativeLayout noContactLayout;

    private String number;
    private List<String> contactLists;
    private ContactAdapter adapter;


    @Override
    protected void initView(View view) {


        initContact();

        //有联系人数据
        if (contactLists != null && contactLists.size() != 0) {
            adapter = new ContactAdapter(getContext(), contactLists);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            contactList.setLayoutManager(linearLayoutManager);
            contactList.setAdapter(adapter);

            adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position == 0) {
                        //跳转到我的群组界面
                        go(MyGroupActivity.class);

                    } else {
                        number = contactLists.get(position - 1);
                        showSelectDialog(number);
                    }
                }
            });

            //没有联系人数据
        } else {

            noContactLayout.setVisibility(View.VISIBLE);

        }


    }

    private void showSelectDialog(final String number) {

        new AlertDialog.Builder(getContext()).setTitle("选择业务").setItems(new String[]{"发送短消息", "拨打电话"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    //发送短信
                    case 0:
                        Intent chatIntent = new Intent(ContactFragment.this.getContext(), ChatActivity.class);
                        chatIntent.putExtra(GlobalVar.INTENT_DATA, number);
                        chatIntent.putExtra(GlobalVar.INTENT_GROUP, false);
                        startActivity(chatIntent);
                        break;
                    //拨打电话
                    case 1:
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        startActivity(intent);
    }


    @Override
    protected int initLayout() {
        return R.layout.fragment_contact;
    }

    private void initContact() {

        contactLists = GlobalVar.getGlobalVar().getContactLists();

        Log.d("sjy", "contact list size:" + contactLists.size());
//        for (int i = 0; i < 10; i++) {
//            contactLists.add("1550112986" + i);
//        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PHONE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(number);
                } else {
                    ToastUtils.showToast(getContext(), "拨打电话权限拒绝,拨号失败");
                }
                break;
            default:
                break;
        }
    }
}
