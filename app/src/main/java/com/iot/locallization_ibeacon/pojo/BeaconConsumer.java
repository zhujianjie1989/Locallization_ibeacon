package com.iot.locallization_ibeacon.pojo;

import android.os.Message;
import android.util.Log;
import java.util.Date;

public class BeaconConsumer
{

    public static void consumeBeacon(Beacon beacon)
    {

        GlobalData.log= "major:"+beacon.major + " minor:" + beacon.minor + " rssi:" + beacon.rssi;
        if (GlobalData.loghandler!=null)
        {
            Message msg = new Message();
            msg.arg1=1;
            GlobalData.loghandler.sendMessage(msg);
        }

        //Log.e("lescon", beacon.major + "  " + beacon.minor + "  " +beacon. rssi + " floor : " + GlabalData.floor);
        if (GlobalData.beaconlist.containsKey("major:"+beacon.major+" minor:"+beacon.minor))
        {
            Log.e("lescon", beacon.major + "  " + beacon.minor + "  " + beacon.rssi + " floor : "+ GlobalData.curr_floor );



            Beacon sensor = GlobalData.beaconlist.get("major:"+beacon.major+" minor:"+beacon.minor);
            sensor.setRssi(beacon.rssi);
            sensor.updateTime = new Date().getTime();

            if ((sensor.rssi - sensor.max_rssi) > -27){
                GlobalData.IPS_flag = true;
                GlobalData.IPS_UpdateTime = new Date();
            }


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

        if (GlobalData.templist.containsKey("major:"+beacon.major+" minor:"+beacon.minor))
        {
            Beacon sensor = GlobalData.templist.get("major:"+beacon.major+" minor:"+beacon.minor);
            sensor.rssi=beacon.rssi;
        }
        else
        {
            GlobalData.templist.put("major:" + beacon.major + " minor:" + beacon.minor, beacon);
        }
    }
}
