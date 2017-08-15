package com.handsome.robot.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.handsome.robot.R;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class NavigationActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private BottomNavigationBar mBottomNavigationBar;
    private static final String TAG = "MainActivity";

    private SizeFragment mSizeFragment;//尺寸测量界面
    private LocationFragment mlocationFragment;//室内测量界面
    private BleFragment mbleFragment;//室内测量界面

    private BleUtils mBleUtils;



    private BluetoothAdapter mBluetoothAdapter;

    int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mBleUtils = new BleUtils();
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
                .addItem(new BottomNavigationItem(R.drawable.fourth_item,"蓝牙配置")).initialise();
        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();

        //检查蓝牙
        init_ble();
        //检查定位权限
        init_right();
    }

    public BleUtils getBleUtils() {
        return mBleUtils;
    }

    public void setBleUtils(BleUtils mBleUtils) {
        this.mBleUtils = mBleUtils;
        if (mSizeFragment!=null){
            mSizeFragment.getTvL().setText(mBleUtils.getLeftDistance());
            mSizeFragment.getTvC().setText(mBleUtils.getCenterDistance());
            mSizeFragment.getTvR().setText(mBleUtils.getRightDistance());
        }
    }

    private void setDefaultFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //采用add方式进行fragment切换
        if (mSizeFragment == null){
            mSizeFragment = SizeFragment.newInstance("尺寸测量");
            transaction.add(R.id.center_main_content,mSizeFragment);
        }
        hideAllFragment(transaction);
        transaction.show(mSizeFragment);

        /*transaction.replace(R.id.center_main_content, mSizeFragment);*/
        transaction.commit();
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction transaction){
        if(mSizeFragment != null){
            transaction.hide(mSizeFragment);
        }
        if(mlocationFragment != null){
            transaction.hide(mlocationFragment);
        }
        if(mbleFragment != null){
            transaction.hide(mbleFragment);
        }
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    private void init_ble()
    {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE))
        {

            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void init_right() {
        if(mBluetoothAdapter.isEnabled()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This app needs location access");
                    builder.setMessage("Please grant location access so this app can detect Bluetooth.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                            }
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Log.d(TAG, "coarse location permission granted");
                    finish();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
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
                    mSizeFragment = SizeFragment.newInstance("尺寸测量");
                    transaction.add(R.id.center_main_content,mSizeFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(mSizeFragment);
                /*transaction.replace(R.id.center_main_content, mSizeFragment);*/
                break;
            case 1:
                if (mlocationFragment == null) {
                    mlocationFragment = LocationFragment.newInstance("室内测量");
                    transaction.add(R.id.center_main_content,mlocationFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(mlocationFragment);
               /* transaction.replace(R.id.center_main_content, mlocationFragment);*/
                break;
            case 2:
                if (mbleFragment == null) {
                    mbleFragment = BleFragment.newInstance("蓝牙配置");
                    transaction.add(R.id.center_main_content,mbleFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(mbleFragment);
               /* transaction.replace(R.id.center_main_content, mbleFragment);*/
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
