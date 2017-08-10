package com.handsome.robot.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.handsome.robot.R;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class NavigationActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private BottomNavigationBar mBottomNavigationBar;
    private static final String TAG = "MainActivity";

    private SizeFragment mSizeFragment;//尺寸测量界面
    private LocationFragment mlocationFragment;//室内测量界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mBottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        // TODO 设置模式
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        //设置背景色样式
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setActiveColor(R.color.white);
        mBottomNavigationBar.setInActiveColor(R.color.gray);
        mBottomNavigationBar.setBarBackgroundColor(R.color.royalblue);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.first_item,"尺寸测量"))
                .addItem(new BottomNavigationItem(R.drawable.second_item,"室内测量"))
                .addItem(new BottomNavigationItem(R.drawable.fourth_item,"蓝牙设置")).initialise();

        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mSizeFragment = SizeFragment.newInstance("尺寸测量");
        transaction.replace(R.id.center_main_content, mSizeFragment);
        transaction.commit();
    }

    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mSizeFragment == null) {
                    mSizeFragment = SizeFragment.newInstance("比赛实况");
                }
                transaction.replace(R.id.center_main_content, mSizeFragment);
                break;

            case 1:
                if (mlocationFragment == null) {
                    mlocationFragment = LocationFragment.newInstance("球员分析");
                }
                transaction.replace(R.id.center_main_content, mlocationFragment);
                break;
            case 2:
                break;
        }
        // 事务提交
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {
        Log.d(TAG, "onTabUnselected() called with: " + "position = [" + position + "]");
    }

    @Override
    public void onTabReselected(int position) {

    }
}
