package com.handsome.robot.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.handsome.robot.Activity.service.BluetoothLeService;

/**
 * Created by Joern on 2017/08/13.
 */

public class BleUtils {
    private String mDeviceName;
    private String mDeviceAddress;
    private String mRssi;

    private String mLeftDistance;
    private String mCenterDistance;
    private String mRightDistance;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
    }

    public String getRssi() {
        return mRssi;
    }

    public void setRssi(String mRssi) {
        this.mRssi = mRssi;
    }

    public String getLeftDistance() {
        return mLeftDistance;
    }

    public void setLeftDistance(String mLeftDistance) {
        this.mLeftDistance = mLeftDistance;
    }

    public String getCenterDistance() {
        return mCenterDistance;
    }

    public void setCenterDistance(String mCenterDistance) {
        this.mCenterDistance = mCenterDistance;
    }

    public String getRightDistance() {
        return mRightDistance;
    }

    public void setRightDistance(String mRightDistance) {
        this.mRightDistance = mRightDistance;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public BluetoothLeService getBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }
}
