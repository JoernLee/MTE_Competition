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

    private Float mLeftDistance = 0f;
    private Float mCenterDistance = 0f;
    private Float mRightDistance = 0f;

    private float leftFloatDistance;
    private float centerFloatDistance;
    private float rightFloatDistance;



    //判断当前运行的6个碎片
    private int nowFragmentNumber = 0;

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

    public Float getLeftDistance() {
        return mLeftDistance;
    }

    public void setLeftDistance(Float mLeftDistance) {
        this.mLeftDistance = mLeftDistance;
    }

    public Float getCenterDistance() {
        return mCenterDistance;
    }

    public void setCenterDistance(Float mCenterDistance) {
        this.mCenterDistance = mCenterDistance;
    }

    public Float getRightDistance() {
        return mRightDistance;
    }

    public void setRightDistance(Float mRightDistance) {
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

    public float getLeftXLocation() {
        return leftFloatDistance;
    }

    public void setLeftXLocation(float leftFloatDistance) {
        this.leftFloatDistance = leftFloatDistance;
    }

    public float getCenterYLocation() {
        return centerFloatDistance;
    }

    public void setCenterYLocation(float centerFloatDistance) {
        this.centerFloatDistance = centerFloatDistance;
    }

    public float getRightFloatDistance() {
        return rightFloatDistance;
    }

    public void setRightFloatDistance(float rightFloatDistance) {
        this.rightFloatDistance = rightFloatDistance;
    }

    public int getNowFragmentNumber() {
        return nowFragmentNumber;
    }

    public void setNowFragmentNumber(int nowFragmentNumber) {
        this.nowFragmentNumber = nowFragmentNumber;
    }
}
