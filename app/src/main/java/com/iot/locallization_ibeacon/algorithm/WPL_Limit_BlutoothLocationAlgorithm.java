package com.iot.locallization_ibeacon.algorithm;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.iot.locallization_ibeacon.activity.DemoActivity;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.GlobalData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class WPL_Limit_BlutoothLocationAlgorithm extends BluetoothLocalizationAlgorithm {

	private int length=6;   // select a few number of signal
	private Handler handler;
	public void setHandler(Handler h){
		handler=h;
	}
	@Override
	public void DoLocalization( )
		{
			Double rssi_max=-40.0;
			Double rssi_min=-150.0;
			Double sumDef=0.0;
			double x= 0;
			double y =0;
			List<Beacon> sensorList = SortWifiSignal();
			DemoActivity.logstring =" minor:"+sensorList.get(0).minor+"rssi:"+(sensorList.get(0).rssi-sensorList.get(0).max_rssi)+" ";
			DemoActivity.logstring +=" minor:"+sensorList.get(1).minor+"rssi:"+(sensorList.get(1).rssi-sensorList.get(1).max_rssi)+" ";
			DemoActivity.logstring +=" minor:"+sensorList.get(2).minor+"rssi:"+(sensorList.get(2).rssi-sensorList.get(2).max_rssi)+"";

			handler.sendMessage(new Message());


			int len=0;
			if (length>sensorList.size())
			{
				len=sensorList.size();
			}
			else {
				len=length;
			}
			
			for (int i = 0; i < len; i++) {
				Beacon sensor = sensorList.get(i);

				if (sensor.rssi <= -100)
					continue;

				rssi_max = sensor.max_rssi*1.0;

				Double tmprssi = rssi_max-sensor.rssi;
				if (tmprssi>rssi_max-rssi_min)
					tmprssi=rssi_max-rssi_min;
				Double def= 1.0 / Math.pow(10, (0.8*tmprssi/10));
				sumDef=sumDef+def;
				x= x + def * sensor.position.latitude;
				y= y + def * sensor.position.longitude;
			}
			x = x / sumDef;
			y = y / sumDef;
			GlobalData.currentPosition = new LatLng(x,y);

			Date time = new Date();
			for (int i = 0 ; i< sensorList.size()  ;i++)
			{
				//Log.e("timeeeee",sensorList.get(i).minor+"   "+(time.getTime()-sensorList.get(i).updateTime.getTime())/1000+"");
				if ((time.getTime()-sensorList.get(i).updateTime)/1000 > 4)
					sensorList.get(i).rssi = -150;
			}

		}
	
	private List<Beacon> SortWifiSignal()
	{
		List<Beacon> signalList = new ArrayList<Beacon>();
		Iterator<Entry<String, Beacon>> iter = GlobalData.beaconlist.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Beacon> entry = (Entry<String, Beacon>) iter.next();
			Beacon sensor = entry.getValue();
			signalList.add(sensor);
		}
		
		for (int i = 0; i < signalList.size()-1; i++) {
			for (int j = i+1; j < signalList.size(); j++) {
				int rssi_i=signalList.get(i).rssi-signalList.get(i).max_rssi;
				int rssi_j=signalList.get(j).rssi-signalList.get(j).max_rssi;
				if (rssi_i<rssi_j)
				{
					Beacon s=signalList.remove(j);
					signalList.add(i, s);
				}
			}
		}
		return signalList;
	}

}
