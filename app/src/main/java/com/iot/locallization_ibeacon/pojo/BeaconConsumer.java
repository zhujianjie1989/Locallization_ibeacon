package com.iot.locallization_ibeacon.pojo;

import android.os.Message;
import android.util.Log;
import java.util.Date;

public class BeaconConsumer
{

    public static void consumeBeacon(Beacon beacon)
    {

        /*GlobalData.log= "major:"+beacon.major + " minor:" + beacon.minor + " rssi:" + beacon.rssi;
        if (GlobalData.loghandler!=null)
        {
            Message msg = new Message();
            msg.arg1=1;
            GlobalData.loghandler.sendMessage(msg);
        }
        // Log.e("lescon", beacon.major + "  " + beacon.minor + "  " +beacon. rssi + " floor : " + GlobalData.curr_floor);
        //Log.e("lescon", "GlobalData.beaconlist.size = " +GlobalData.beaconlist.size()+" beacon.ID = "+beacon.ID);*/

        if (GlobalData.beaconlist.containsKey(beacon.ID))
        {


            Beacon sensor = GlobalData.beaconlist.get(beacon.ID);
            sensor.setRssi(beacon.rssi);
            sensor.updateTime = new Date().getTime();

            //类似心跳，判断是否还在beacon范围，如果超出beaco范围，6s后切换到GPS定位
            if ((sensor.rssi - sensor.max_rssi) > -27){

                GlobalData.scanbeaconlist.put(sensor.ID,sensor);//添加到当前可扫描到的beacon列表

                GlobalData.IPS_UpdateTime = new Date(); //更新扫描到beacon的时间
            }

            //判断是否楼成切换
            if (GlobalData.curr_floor != sensor.floor && (sensor.rssi - sensor.max_rssi) > -27 )
            {
                GlobalData.curr_floor = sensor.floor;
                if (GlobalData.loghandler!=null)
                {
                    Message msg = new Message();
                    msg.arg1=2;
                    GlobalData.loghandler.sendMessage(msg);
                }
                Log.e("lescon", "floor = " + GlobalData.curr_floor);
            }
        }

        //-----------------------------------------------------------------
        //在配置beacon时用到，用于获取当前
        if (GlobalData.templist.containsKey(beacon.ID))
        {
            Beacon sensor = GlobalData.templist.get(beacon.ID);
            sensor.rssi=beacon.rssi;
            //如果该beacon的信号已经衰弱到很低的程度，移除临时列表
            if (sensor.rssi - sensor.max_rssi > -30)
            {
                GlobalData.templist.remove(beacon.ID);
            }
        }
        else
        {

            if (beacon.rssi > -110)
            {
                GlobalData.templist.put(beacon.ID, beacon);
            }

        }
        //-------------------------------------------------------------------
    }
}
