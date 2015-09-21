package com.iot.locallization_ibeacon.pojo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.iot.locallization_ibeacon.tools.Tools;

/**
 * Created by zhujianjie on 3/6/2015.
 */
public class BLEScanCallBack implements BluetoothAdapter.LeScanCallback {
    @Override
    public void onLeScan( BluetoothDevice device, int rssi,  byte[] scanRecord){

        if (rssi > -20)
            return;

        Beacon beacon = Tools.dealScan(device, rssi, scanRecord);
        BeaconConsumer.consumeBeacon(beacon);
    }
}
