package com.iot.locallization_ibeacon.pojo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by zhujianjie on 3/6/2015.
 */
public class ScanServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("ScanServiceConnection","stopTimer");

    }
}
