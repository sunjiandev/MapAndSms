package com.sunkaisens.gisandsms;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sunkaisens.gisandsms.base.BaseActivity;
import com.sunkaisens.gisandsms.tabcontact.ContactFragment;
import com.sunkaisens.gisandsms.tabcontact.SettingActivity;
import com.sunkaisens.gisandsms.tabmessage.MessageFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author:sun
 * @date:2018/12/26
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void initView() {
        int position = getIntent().getIntExtra(GlobalVar.INTENT_DATA, 0);
        setSupportActionBar(mainToolbar);
        tabLayout.setupWithViewPager(viewPager);
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(new MessageFragment());
        fragments.add(new ContactFragment());
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), this, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
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
}
