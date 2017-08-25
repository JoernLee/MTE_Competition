package com.handsome.robot.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handsome.robot.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joern on 2017/08/22.
 */

public class DrawerActivity extends FragmentActivity implements SensorEventListener {
    /** DrawerLayout */
    private DrawerLayout mDrawerLayout;
    /** 左边栏菜单 */
    private ListView mMenuListView;

    private final String[] mMenuTitles = new String[] { "三激光距离测量", "室内测量", "平面倾斜度测试",
            "平面标定", "空间标定", "图像测距" ,"蓝牙设置"};

    /** 菜单打开/关闭状态 */
    private boolean isDirection_left = false;

    private View showView;


    private NewLazerFragment lazerFragment;//三激光测量
    private NewRoomFragment roomFragment;//室内测量
    private NewLeanFragment leanFragment;//平面倾斜度测试
    private NewPlaneFragment planeFragment;//平面标定
    private NewSpaceFragment spaceFragment;//空间标定
    private  NewMeasureFragment measureFragment;//图像测距
    private  BleFragment bleFragment;//蓝牙设置

    private BleUtils mBleUtils = new BleUtils(); //蓝牙工具
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    /* 获取定位所用工具*/
    private LocationManager locationManager;
    private String provider;
    private Location mLocation;

    int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 2;

    /* 传感器工具 */
    private SensorManager sensorManager = null;
    private Sensor acc_sensor;
    private Sensor mag_sensor;
    //加速度传感器数据
    float accValues[]=new float[3];
    //地磁传感器数据
    float magValues[]=new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[]=new float[9];

    //模拟方向传感器的数据（原始数据为弧度）
    float values[]=new float[3];

    DecimalFormat decimalFormat=new DecimalFormat(".00");

    //判断当前运行的碎片是哪一个
    private int nowFragmentNumber = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        //初始化抽屉以及左边菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuListView = (ListView) findViewById(R.id.left_drawer);

        this.showView = mMenuListView;

        List<Map<String, Object>> list=getData();

        mMenuListView.setAdapter(new MyListAdapter(this, list));

        mMenuListView.setOnItemClickListener(new DrawerItemClickListener());

        // 设置抽屉打开时，主要内容区被自定义阴影覆盖
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // 设置ActionBar可见，并且切换菜单和内容视图
     /*   getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);*/

        mDrawerLayout.addDrawerListener(new DrawerLayoutStateListener());
        if (savedInstanceState == null) {
            // selectItem(0);
            setDefaultFragment();
        }

        //检查蓝牙
        init_ble();
        //检查定位权限
        init_right();
        //设置位置管理器
        init_location();
        //设置传感器管理器
        init_sensor();

      /*  if (savedInstanceState == null) {
            selectItem(0);
        }*/

    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //采用add方式进行fragment切换
        if (lazerFragment == null) {
            lazerFragment = NewLazerFragment.newInstance("三激光距离测量");
            transaction.add(R.id.center_main_content, lazerFragment);
        }
        hideAllFragment(transaction);
        transaction.show(lazerFragment);
        transaction.commit();
    }

    private void init_ble() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {

            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void init_right() {
        if (mBluetoothAdapter.isEnabled()) {
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
                if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
                }
            }
        }
    }

    private void init_location(){
        //获取定位服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);
        provider = LocationManager.NETWORK_PROVIDER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mLocation = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(provider, 1000, 2, locationListener);
            }
        }else {
            mLocation = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 1000, 2, locationListener);
        }

    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location arg0) {
            // TODO Auto-generated method stub
            // 更新当前经纬度
            mLocation = arg0;
        }
    };

    private void init_sensor(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //加速度传感器和地磁传感器
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //给传感器注册监听：
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mag_sensor,SensorManager.SENSOR_DELAY_GAME);

    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public Location getLocation() {
        return mLocation;
    }

    public BleUtils getBleUtils() {
        return mBleUtils;
    }

    public float[] getValues() {
        return values;
    }

    //这个方法是在BleFragment里面调用-用来修改两个Fragment的对于参数UI
    public void setBleUtils(BleUtils mBleUtils) {
        this.mBleUtils = mBleUtils;
        switch (nowFragmentNumber){
            case 0:
                if (lazerFragment != null){
                    lazerFragment.getTvL().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    lazerFragment.getTvC().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterDistance())) + "cm" );
                    lazerFragment.getTvR().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm" );

                    lazerFragment.getTvImagePosZ().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    lazerFragment.getTvImagePosX().setText(decimalFormat.format(values[1]) + "°");
                    lazerFragment.getTvImagePosY().setText(decimalFormat.format(values[2]) + "°");
                }
                break;
            case 1:
                if (roomFragment != null){
                    roomFragment.getTvL().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    roomFragment.getTvR().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm" );

                    roomFragment.getTvImagePosZ().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    roomFragment.getTvImagePosX().setText(decimalFormat.format(values[1]) + "°");
                    roomFragment.getTvImagePosY().setText(decimalFormat.format(values[2]) + "°");
                }
                break;
            case 2:
                if (leanFragment != null){
                    leanFragment.getTvL().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    leanFragment.getTvC().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterDistance())) + "cm" );
                    leanFragment.getTvR().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm" );

                    leanFragment.getTvImagePosZ().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    leanFragment.getTvImagePosX().setText(decimalFormat.format(values[1]) + "°");
                    leanFragment.getTvImagePosY().setText(decimalFormat.format(values[2]) + "°");
                }
                break;
            case 3:
                if (planeFragment != null){
                    planeFragment.getTvL_1().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    planeFragment.getTvL_2().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    planeFragment.getTvC_2().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterDistance())) + "cm" );
                    planeFragment.getTvR_1().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm" );

                    planeFragment.getTvImagePosZ_1().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    planeFragment.getTvImagePosZ_2().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    planeFragment.getTvImagePosX_1().setText(decimalFormat.format(values[1]) + "°");
                    planeFragment.getTvImagePosX_2().setText(decimalFormat.format(values[1]) + "°");
                    planeFragment.getTvImagePosY_1().setText(decimalFormat.format(values[2]) + "°");
                    planeFragment.getTvImagePosY_2().setText(decimalFormat.format(values[2]) + "°");
                }
                break;
            case 4:
                if (spaceFragment != null){
                    spaceFragment.getTvL_1().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    spaceFragment.getTvL_2().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    spaceFragment.getTvC_2().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterDistance())) + "cm" );
                    spaceFragment.getTvR_1().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm" );

                    spaceFragment.getTvImagePosZ_1().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    spaceFragment.getTvImagePosZ_2().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    spaceFragment.getTvImagePosX_1().setText(decimalFormat.format(values[1]) + "°");
                    spaceFragment.getTvImagePosX_2().setText(decimalFormat.format(values[1]) + "°");
                    spaceFragment.getTvImagePosY_1().setText(decimalFormat.format(values[2]) + "°");
                    spaceFragment.getTvImagePosY_2().setText(decimalFormat.format(values[2]) + "°");
                }
                break;
            case 5:
                if (measureFragment != null){
                    measureFragment.getTvL().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
                    measureFragment.getTvC().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterDistance())) + "cm" );
                    measureFragment.getTvR().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm" );

                    measureFragment.getTvImagePosZ().setText(decimalFormat.format(values[0]+(float)180.00) + "°");
                    measureFragment.getTvImagePosX().setText(decimalFormat.format(values[1]) + "°");
                    measureFragment.getTvImagePosY().setText(decimalFormat.format(values[2]) + "°");
                }
                break;
        }
       /* if (mSizeFragment != null) {
            //mSizeFragment.getTvL().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftDistance())) + "cm" );
            //mSizeFragment.getTvC().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterDistance())) + "cm");
            //mSizeFragment.getTvR().setText(String.valueOf(decimalFormat.format(mBleUtils.getRightDistance())) + "cm");
        }
        if (mlocationFragment != null){
            mlocationFragment.getTvX().setText(String.valueOf(decimalFormat.format(mBleUtils.getLeftXLocation())) + "cm");
            mlocationFragment.getTvY().setText(String.valueOf(decimalFormat.format(mBleUtils.getCenterYLocation())) + "cm");
            mlocationFragment.getTvZ().setText("0cm");
            float[] newLocation = new float[3];
            newLocation[0] = mBleUtils.getLeftXLocation()/60.0f;
            newLocation[1] = mBleUtils.getCenterYLocation()/60.0f;
            newLocation[2] = 0.0f;
            //更新LocationFragment里面平面定位的坐标
            mlocationFragment.setNewArrayPoint(newLocation);
            //
        }*/
    }

    /**
     * ListView上的Item点击事件
     *
     */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //采用add方式进行fragment切换
        switch (position) {
            case 0:
                nowFragmentNumber = 0;
                if (lazerFragment == null) {
                    lazerFragment = NewLazerFragment.newInstance("三激光距离测量");
                    transaction.add(R.id.center_main_content, lazerFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(lazerFragment);

                break;

            case 1:
                nowFragmentNumber = 1;
                if (roomFragment == null) {
                    roomFragment = NewRoomFragment.newInstance("室内测量");
                    transaction.add(R.id.center_main_content, roomFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(roomFragment);

                break;
            case 2:
                nowFragmentNumber = 2;
                if (leanFragment == null) {
                    leanFragment = NewLeanFragment.newInstance("平面倾斜度测试");
                    transaction.add(R.id.center_main_content, leanFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(leanFragment);

                break;
            case 3:
                nowFragmentNumber = 3;
                if (planeFragment == null) {
                    planeFragment = NewPlaneFragment.newInstance("平面标定");
                    transaction.add(R.id.center_main_content, planeFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(planeFragment);
                break;
            case 4:
                nowFragmentNumber = 4;
                if (spaceFragment == null) {
                    spaceFragment = NewSpaceFragment.newInstance("空间标定");
                    transaction.add(R.id.center_main_content, spaceFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(spaceFragment);
                break;
            case 5:
                nowFragmentNumber = 5;
                if (measureFragment == null) {
                    measureFragment = NewMeasureFragment.newInstance("图像测距");
                    transaction.add(R.id.center_main_content, measureFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(measureFragment);
                break;
            case 6:
                nowFragmentNumber = 6;
                if (bleFragment == null) {
                    bleFragment = BleFragment.newInstance("蓝牙设置");
                    transaction.add(R.id.center_main_content, bleFragment);
                }
                //隐藏所有fragment
                hideAllFragment(transaction);
                //显示需要显示的fragment
                transaction.show(bleFragment);
                break;

        }
        // 事务提交
        transaction.commit();

        // update selected item title, then close the drawer
        // 更新选择后的item和title，然后关闭菜单
        mMenuListView.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mMenuListView);
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction transaction) {
        if (lazerFragment != null) {
            transaction.hide(lazerFragment);
        }
        if (roomFragment != null) {
            transaction.hide(roomFragment);
        }
        if (leanFragment != null) {
            transaction.hide(leanFragment);
        }
        if (planeFragment != null) {
            transaction.hide(planeFragment);
        }
        if (spaceFragment != null) {
            transaction.hide(spaceFragment);
        }
        if (measureFragment != null) {
            transaction.hide(measureFragment);
        }
        if (bleFragment != null) {
            transaction.hide(bleFragment);
        }
    }

    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<>();
        String[] strTitle=mMenuTitles;
        Object[] image={R.drawable.ic_drawer,R.drawable.ic_drawer,
                R.drawable.ic_drawer ,R.drawable.ic_drawer,R.drawable.ic_drawer,
                R.drawable.ic_drawer,R.drawable.ic_drawer};
        for(int i =0;i<7;i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image",image[i] );
            map.put("title", strTitle[i]);
            list.add(map);
        }
        return list;
    }

    /**
     * DrawerLayout状态变化监听
     */
    private class DrawerLayoutStateListener extends
            DrawerLayout.SimpleDrawerListener {
        /**
         * 当导航菜单滑动的时候被执行
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            showView = drawerView;
            if (drawerView == mMenuListView) {// 根据isDirection_left决定执行动画
            }
        }

        /**
         * 当导航菜单打开时执行
         */
        @Override
        public void onDrawerOpened(android.view.View drawerView) {
            if (drawerView == mMenuListView) {
                isDirection_left = true;
            }
        }

        /**
         * 当导航菜单关闭时执行
         */
        @Override
        public void onDrawerClosed(android.view.View drawerView) {
            if (drawerView == mMenuListView) {
                isDirection_left = false;
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
                break;
            }
            case PERMISSION_REQUEST_FINE_LOCATION:{
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
                break;
            }
        }
    }
    //传感器状态改变时的回调方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            accValues=event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magValues=event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        /**public static boolean getRotationMatrix (float[] R, float[] I, float[] gravity, float[] geomagnetic)
         * 填充旋转数组r
         * r：要填充的旋转数组
         * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
         * gravity:加速度传感器数据
         * geomagnetic：地磁传感器数据
         */
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        /**
         * public static float[] getOrientation (float[] R, float[] values)
         * R：旋转数组
         * values ：模拟方向传感器的数据
         */

        SensorManager.getOrientation(r, values);

        //将弧度转化为角度后输出
        /*
        values[0]  ：方向角，但用（磁场+加速度）得到的数据范围是（-180～180）,也就是说，0表示正北，90表示正东，180/-180表示正南，-90表示正西。而直接通过方向感应器数据范围是（0～359）360/0表示正北，90表示正东，180表示正南，270表示正西。
        values[1]  pitch 倾斜角  即由静止状态开始，前后翻转，手机顶部往上抬起（0~-180），手机尾部往上抬起（0~180）
        values[2]  roll 旋转角 即由静止状态开始，左右翻转，手机左侧抬起（0~180）,手机右侧抬起（0~-180）
        */
        for(int i=0;i<3;i++){
            values[i]=(float) Math.toDegrees(values[i]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
