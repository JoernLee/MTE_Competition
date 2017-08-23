package com.handsome.robot.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.handsome.robot.R;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;

import java.text.DecimalFormat;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener,SensorEventListener {
    private BottomNavigationBar mBottomNavigationBar;
    private static final String TAG = "MainActivity";

    private NewMeasureFragment mSizeFragment;//尺寸测量界面
    private LocationFragment mlocationFragment;//室内测量界面
    private BleFragment mbleFragment;//室内测量界面


    private BleUtils mBleUtils; //蓝牙工具
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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        mBleUtils = new BleUtils();
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        // TODO 设置模式
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        //设置背景色样式
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setActiveColor(R.color.white);
        mBottomNavigationBar.setInActiveColor(R.color.gray);
        mBottomNavigationBar.setBarBackgroundColor(R.color.royalblue);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.firstitem, "Measurement"))
                .addItem(new BottomNavigationItem(R.drawable.seconditem, "Location"))
                .addItem(new BottomNavigationItem(R.drawable.thirditem, "Bluetooth")).initialise();
        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();


        //检查蓝牙
        init_ble();
        //检查定位权限
        init_right();
        //设置位置管理器
        init_location();
        //设置传感器管理器
        init_sensor();

    }



    private void setDefaultFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //采用add方式进行fragment切换
        if (mSizeFragment == null) {
            mSizeFragment = NewMeasureFragment.newInstance("尺寸测量");
            transaction.add(R.id.center_main_content, mSizeFragment);
        }
        hideAllFragment(transaction);
        transaction.show(mSizeFragment);

        /*transaction.replace(R.id.center_main_content, mSizeFragment);*/
        transaction.commit();
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction transaction) {
        if (mSizeFragment != null) {
            transaction.hide(mSizeFragment);
        }
        if (mlocationFragment != null) {
            transaction.hide(mlocationFragment);
        }
        if (mbleFragment != null) {
            transaction.hide(mbleFragment);
        }
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
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
    }

    private void init_sensor(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //加速度传感器和地磁传感器
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //给传感器注册监听：
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mag_sensor,SensorManager.SENSOR_DELAY_GAME);

    }

    public Location getLocation() {
        return mLocation;
    }

    public BleUtils getBleUtils() {
        return mBleUtils;
    }

    //这个方法是在BleFragment里面调用-用来修改两个Fragment的对于参数UI
    public void setBleUtils(BleUtils mBleUtils) {
        this.mBleUtils = mBleUtils;
        if (mSizeFragment != null) {
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
        }
    }

    public float[] getValues() {
        return values;
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

    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager fm = this.getFragmentManager();

        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mSizeFragment == null) {
                    mSizeFragment = NewMeasureFragment.newInstance("尺寸测量");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
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
}
