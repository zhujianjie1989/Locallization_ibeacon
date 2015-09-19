package com.iot.locallization_ibeacon.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.iot.locallization_ibeacon.pojo.BLEScanCallBack;

import java.util.Timer;
import java.util.TimerTask;

public class ScanBluetoothService extends Service {
    private TimerTask task;
    private BluetoothAdapter bluetoothAdapter;
    private static Timer timer = null;
    private BLEScanCallBack leScanCallback = new BLEScanCallBack();

    @Override
    public IBinder onBind(Intent intent) {
        initBlueTooth();
        initTask();
        return  new Binder();
    }

    private void  initBlueTooth()  {

        try
        {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
            {
                throw new Exception("Bluetooth is not available");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
    public void initTask(){

        task = new TimerTask() {
            @Override
            public void run() {
                bluetoothAdapter.startLeScan(leScanCallback);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                bluetoothAdapter.stopLeScan(leScanCallback);

            }
        };

        timer = new Timer();
        timer.schedule(task,0, 100);
    }

    public static void stopTimer()
    {
        timer.cancel();
    }



}
