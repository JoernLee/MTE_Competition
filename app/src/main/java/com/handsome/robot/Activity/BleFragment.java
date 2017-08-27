package com.handsome.robot.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.handsome.robot.Activity.service.BluetoothLeService;
import com.handsome.robot.Activity.ui.Ble_Activity;
import com.handsome.robot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.BIND_AUTO_CREATE;
import static com.handsome.robot.Activity.ui.Ble_Activity.HEART_RATE_MEASUREMENT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BleFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";


    // TODO: Rename and change types of parameters
    private String mParam1;
    //扫描蓝牙按钮
    private Button btnScan;
    //取消蓝牙连接按钮
    private Button btnDiscoonect;
    //蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    // 蓝牙信号强度
    private ArrayList<Integer> rssis;
    // 自定义Adapter
    LeDeviceListAdapter mleDeviceListAdapter;

    //蓝牙连接状态
    private boolean mConnected = false;
    private String status = "disconnected";

    //蓝牙特征值
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private static BluetoothGattCharacteristic target_chara = null;

    //控制左中右红外测距的开关
    private Button btnLeftOp;
    private Button btnCenterOp;
    private Button btnRightOp;

    private ListView lvDevice;
    private TextView tvStatus;

    // 描述扫描蓝牙的状态
    private boolean mScanning;
    private boolean scan_flag;
    private Handler mHandler = new Handler();
    private Handler myHandler = new Handler();
    private BleUtils fBleUtils;
    //蓝牙服务
    private static BluetoothLeService mBluetoothLeService;

    //设备信息
    public String deviceName;
    public String deviceAddress;
    public String deviceRssi;

    public NavigationActivity mNavActivity;
    public DrawerActivity mDrawerActivity;
    private ImageView imageReturn;


    // 蓝牙扫描时间
    private static final long SCAN_PERIOD = 10000;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


   /* private OnFragmentInteractionListener mListener;*/

    public BleFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BleFragment newInstance(String param1) {
        BleFragment fragment = new BleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ble, container, false);
        Bundle bundle = getArguments();
        scan_flag = true;
        btnScan = (Button) view.findViewById(R.id.btn_scan_dev);
        btnScan.setOnClickListener(this);

        btnDiscoonect = (Button)view.findViewById(R.id.btn_disconnect);
        btnDiscoonect.setOnClickListener(this);
        btnDiscoonect.setEnabled(false);

        btnLeftOp = (Button) view.findViewById(R.id.btn_left_operation);
        btnLeftOp.setOnClickListener(this);

        btnCenterOp = (Button) view.findViewById(R.id.btn_center_operation);
        btnCenterOp.setOnClickListener(this);

        btnRightOp = (Button) view.findViewById(R.id.btn_right_operation);
        btnRightOp.setOnClickListener(this);

        tvStatus = (TextView) view.findViewById(R.id.tv_ble_status);

        lvDevice = (ListView) view.findViewById(R.id.lv_device);
        lvDevice.setAdapter(mleDeviceListAdapter);

        imageReturn = (ImageView)view.findViewById(R.id.iv_return_ble);

        imageReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerActivity.getmDrawerLayout().openDrawer(mDrawerActivity.getmMenuListView());
            }
        });



        /* listview点击函数 */
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {
                // TODO Auto-generated method stub
                final BluetoothDevice device = mleDeviceListAdapter
                        .getDevice(position);
                fBleUtils.setDeviceName(device.getName());
                fBleUtils.setDeviceAddress(device.getAddress());
                fBleUtils.setRssi(rssis.get(position).toString());
                fBleUtils.setBluetoothDevice(device);
                fBleUtils.setBluetoothAdapter(mBluetoothAdapter);


                deviceName = device.getName();
                deviceAddress = device.getAddress();
                deviceRssi = rssis.get(position).toString();

                if (device == null)
                    return;
                if (mScanning) {
                    /* 停止扫描设备 */
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                try {
                    //启动蓝牙服务
                    Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
                    getActivity().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    //启动Ble_Activity
                    getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                    if (mBluetoothLeService != null) {
                        //根据蓝牙地址，建立连接
                        final boolean result = mBluetoothLeService.connect(deviceAddress);
                        Log.d(TAG, "Connect request result=" + result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });



       /* final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();*/

        return view;
    }

    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            // 根据蓝牙地址，连接设备
            mBluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothLeService = null;
    }

    //TODO:离开Fragment时第一个调用这个方法，需要保存获得的参数
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    //下面是蓝牙广播接收器模块-用来接收来自别的蓝牙的消息
    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
            {
                mConnected = true;
                status = "connected";
                //更新连接状态
                updateConnectionState(status);
                System.out.println("BroadcastReceiver :" + "device connected");
                Toast.makeText(getActivity(),"AM_Tool连接成功！",Toast.LENGTH_SHORT).show();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
                    .equals(action)) {
                mConnected = false;
                status = "disconnected";
                //更新连接状态
                updateConnectionState(status);
                System.out.println("BroadcastReceiver :"
                        + "device disconnected");
                Toast.makeText(getActivity(),"AM_Tool连接失败！",Toast.LENGTH_SHORT).show();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
                    .equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                //获取设备的所有蓝牙服务
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
                System.out.println("BroadcastReceiver :"
                        + "device SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
            {
                //处理发送过来的数据
               displayData(intent.getExtras().getString(
                        BluetoothLeService.EXTRA_DATA));
                System.out.println("BroadcastReceiver onData:"
                        + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    /* 更新连接状态 */
    private void updateConnectionState(String status) {
        tvStatus.setText(status);
        if (status.equals("connected")){
            btnDiscoonect.setEnabled(true);
        }else {
            btnDiscoonect.setEnabled(false);
        }
       /* Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("connect_state", status);
        msg.setData(b);
        //将连接状态更新的UI的textview上
        myHandler.sendMessage(msg);
        System.out.println("connect_state:" + status);*/
    }


    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * @param @param rev_string(接受的数据)
     * @return void
     * @throws
     * @Title: displayData
     * @Description: TODO(接收到的数据另外两个Fragment中显示！！！)
     */
    private void displayData(final String rev_string) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] direction = rev_string.split("=");
                if (direction[0].equals("left_distance")){
                    fBleUtils.setLeftXLocation(60.0f - Float.parseFloat(direction[1]));
                    fBleUtils.setLeftDistance(Float.parseFloat(direction[1]));

                }else if (direction[0].equals("center_distance")){
                    fBleUtils.setCenterYLocation(60.0f - Float.parseFloat(direction[1]));
                    fBleUtils.setCenterDistance(Float.parseFloat(direction[1]));
                }else if (direction[0].equals("right_distance")){
                    fBleUtils.setRightFloatDistance(Float.parseFloat(direction[1]));
                    fBleUtils.setRightDistance(Float.parseFloat(direction[1]));
                }
                tvStatus.setText(rev_string);
                mDrawerActivity.setBleUtils(fBleUtils);
                btnDiscoonect.setEnabled(true);
            }
        });
    }

    /**
     * @return void
     * @throws
     * @Title: displayGattServices
     * @Description: TODO(处理蓝牙服务)
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";

        // 服务数据,可扩展下拉列表的第一级数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // 特征数据（隶属于某一级服务下面的特征值集合）
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        // 部分层次，所有特征值集合
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            // 获取服务列表
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

            gattServiceData.add(currentServiceData);

            System.out.println("Service uuid:" + uuid);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

            // 从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString()
                        .equals(HEART_RATE_MEASUREMENT)) {
                    // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mBluetoothLeService
                                    .readCharacteristic(gattCharacteristic);
                        }
                    }, 200);

                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBluetoothLeService.setCharacteristicNotification(
                            gattCharacteristic, true);
                    target_chara = gattCharacteristic;
                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    // mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic
                        .getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    System.out.println("---descriptor UUID:"
                            + descriptor.getUuid());
                    // 获取特征值的描述
                    mBluetoothLeService.getCharacteristicDescriptor(descriptor);
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    // true);
                }

                gattCharacteristicGroupData.add(currentCharaData);
            }
            // 按先后顺序，分层次放入特征值集合中，只有特征值
            mGattCharacteristics.add(charas);
            // 构件第二级扩展列表（服务下面的特征值）
            gattCharacteristicData.add(gattCharacteristicGroupData);

        }

    }


    /*
     * 按钮响应事件-扫描设备以及控制左中右红外测距器开关
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_scan_dev:
                if (scan_flag) {
                    boolean gps_able = false;
                    mleDeviceListAdapter = new LeDeviceListAdapter();
                    lvDevice.setAdapter(mleDeviceListAdapter);
                    gps_able = isGpsEnable(getActivity());
                    if (gps_able == true) {
                        scanLeDevice(true);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        int requestCode = 1;
                        this.startActivityForResult(intent, requestCode);
                    }

                } else {
                    scanLeDevice(false);
                    btnScan.setText("Scan Device");
                }
                break;
            //TODO:选择关闭和开启的红外测距模块
            case R.id.btn_left_operation:
                if (btnLeftOp.getText().equals("L_Open")) {
                    Toast.makeText(getActivity(),"打开左边红外测距器",Toast.LENGTH_SHORT).show();
                    target_chara.setValue("3");
                    //调用蓝牙服务的写特征值方法实现发送数据
                    mBluetoothLeService.writeCharacteristic(target_chara);
                    btnLeftOp.setText(R.string.left_close_status);
                } else {
                    Toast.makeText(getActivity(),"关闭左边红外测距器",Toast.LENGTH_SHORT).show();
                    target_chara.setValue("2");
                    //调用蓝牙服务的写特征值方法实现发送数据
                    mBluetoothLeService.writeCharacteristic(target_chara);
                    btnLeftOp.setText(R.string.left_open_status);
                }
                break;
            case R.id.btn_center_operation:
                if (btnCenterOp.getText().equals("C_Open")) {
                    Toast.makeText(getActivity(),"打开中间红外测距器",Toast.LENGTH_SHORT).show();
                    target_chara.setValue("5");
                    //调用蓝牙服务的写特征值方法实现发送数据
                    mBluetoothLeService.writeCharacteristic(target_chara);
                    btnCenterOp.setText(R.string.center_close_status);
                } else {
                    Toast.makeText(getActivity(),"关闭中间红外测距器",Toast.LENGTH_SHORT).show();
                    target_chara.setValue("4");
                    //调用蓝牙服务的写特征值方法实现发送数据
                    mBluetoothLeService.writeCharacteristic(target_chara);
                    btnCenterOp.setText(R.string.center_open_status);
                }
                break;
            case R.id.btn_right_operation:
                if (btnRightOp.getText().equals("R_Open")) {
                    Toast.makeText(getActivity(),"打开右边红外测距器",Toast.LENGTH_SHORT).show();
                    target_chara.setValue("7");
                    //调用蓝牙服务的写特征值方法实现发送数据
                    mBluetoothLeService.writeCharacteristic(target_chara);
                    btnRightOp.setText(R.string.right_close_status);
                } else {
                    Toast.makeText(getActivity(),"关闭右边红外测距器",Toast.LENGTH_SHORT).show();
                    target_chara.setValue("6");
                    //调用蓝牙服务的写特征值方法实现发送数据
                    mBluetoothLeService.writeCharacteristic(target_chara);
                    btnRightOp.setText(R.string.right_open_status);
                }
                break;
            case R.id.btn_disconnect:
                //解除广播接收器
                getActivity().unregisterReceiver(mGattUpdateReceiver);
                mBluetoothLeService.disconnect();
                /*mBluetoothLeService = null;*/
                btnDiscoonect.setEnabled(false);
                Toast.makeText(getActivity(),"解除蓝牙连接",Toast.LENGTH_SHORT).show();
        }
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
           /* mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            scan_flag = true;
                            btnScan.setText("扫描设备");
                            Log.i("SCAN", "stop.....................");
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    });
                }
            }, SCAN_PERIOD);*/
			/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
            Log.i("SCAN", "begin.....................");
            mScanning = true;
            scan_flag = false;
            btnScan.setText("Stop Scanning");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //TODO:版本
			/*if(android.os.Build.VERSION.SDK_INT<21)
				bluetoothAdapter.startLeScan(this);
			else{
				bluetoothLeScanner.startScan(callBack);
			}*/
        } else {
            Log.i("Stop", "stoping................");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scan_flag = true;
        }

    }

    // Gps是否可用
    public static final boolean isGpsEnable(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 蓝牙扫描回调函数 实现扫描蓝牙设备，回调蓝牙BluetoothDevice，可以获取name MAC等信息
     **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            // TODO Auto-generated method stub

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
					/* 讲扫描到设备的信息输出到listview的适配器 */
                    mleDeviceListAdapter.addDevice(device, rssi);
                    mleDeviceListAdapter.notifyDataSetChanged();
                }
            });

            System.out.println("Address:" + device.getAddress());
            System.out.println("Name:" + device.getName());
            System.out.println("rssi:" + rssi);

        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDrawerActivity = (DrawerActivity) activity;
        fBleUtils = mDrawerActivity.getBleUtils();
        mBluetoothAdapter = mDrawerActivity.getmBluetoothAdapter();
       /* ((NavigationActivity) activity).setHandler(myHandler);*/
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;

        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            rssis = new ArrayList<Integer>();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getActivity().getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                rssis.add(rssi);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            rssis.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * 重写getview
         **/
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            // General ListView optimization code.
            // 加载listview每一项的视图
            view = mInflator.inflate(R.layout.listitem, null);
            // 初始化三个textview显示蓝牙信息
            TextView deviceAddress = (TextView) view
                    .findViewById(R.id.tv_deviceAddr);
            TextView deviceName = (TextView) view
                    .findViewById(R.id.tv_deviceName);
            TextView rssi = (TextView) view.findViewById(R.id.tv_rssi);

            BluetoothDevice device = mLeDevices.get(i);
            deviceAddress.setText(device.getAddress());
            deviceName.setText(device.getName());
            rssi.setText("" + rssis.get(i));

            return view;
        }
    }

   /* *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
