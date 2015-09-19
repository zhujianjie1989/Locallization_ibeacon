package com.iot.locallization_ibeacon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iot.locallization_ibeacon.R;
import com.iot.locallization_ibeacon.pojo.ScanServiceConnection;
import com.iot.locallization_ibeacon.service.ScanBluetoothService;
import com.iot.locallization_ibeacon.tools.Tools;

import java.io.File;


public class MainActivity extends Activity {

    private ScanServiceConnection sc;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readConf();
        init_Button();
        initService();
    }

    public void readConf()
    {
        File file = new File(Tools.path);
        if(file.exists())
        {
            Tools.ReadConfigFile();
        }
    }

    public void initService()
    {
        sc= new ScanServiceConnection();
        Intent service = new Intent(MainActivity.this,ScanBluetoothService.class);
        bindService(service,sc,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(sc);
        ScanBluetoothService.stopTimer();
        Log.e("localliziton","onDestroy");
    }

    private void  init_Button()
    {
        Button Get_Lanlng = (Button) findViewById(R.id.BT_Get_Lanlng);
        Button Demo = (Button) findViewById(R.id.BT_Demo);

        Get_Lanlng.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, InitBeaconPositionActivity.class);
                startActivity(intent);
            }
        });

        Demo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, DemoActivity.class);
                startActivity(intent);
            }
        });

    }

}

