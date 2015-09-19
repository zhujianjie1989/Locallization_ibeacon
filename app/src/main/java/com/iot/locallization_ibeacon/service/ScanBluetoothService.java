package com.iot.locallization_ibeacon.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.iot.locallization_ibeacon.pojo.CallBack;

import java.util.Timer;
import java.util.TimerTask;

public class ScanBluetoothService extends Service {
    private TimerTask task;
    private Boolean on_off = false;
    private Handler handler = null;
    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private  static Timer timer = null;
    private BluetoothAdapter.LeScanCallback leScanCallback = new CallBack();

    @Override
    public IBinder onBind(Intent intent) {
        initBlueTooth();
        initHandler();
        initTask();
        return  new Binder();
    }

    public void initHandler()
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                on_off = !on_off;
                if (on_off)
                {
                    bluetoothAdapter.startLeScan(leScanCallback);
                    Log.e("mBluetoothAdapter","startLeScan");
                }
                else
                {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    Log.e("mBluetoothAdapter", "stopLeScan");
                }
            }
        };
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

        task = new TimerTask()
        {
            @Override
            public void run()
            {

                on_off = !on_off;
                if (on_off)
                {
                    bluetoothAdapter.startLeScan(leScanCallback);
                    Log.e("mBluetoothAdapter","startLeScan");
                }
                else
                {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    Log.e("mBluetoothAdapter", "stopLeScan");
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 1000, 800);
    }

    public static  void stopTimer()
    {
        timer.cancel();
    }



}
