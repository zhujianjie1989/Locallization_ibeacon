package com.iot.locallization_ibeacon.pojo;

import android.os.Environment;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;

public class GlobalData
{
	public static String log;
	public static  int curr_floor=4;
	public static Handler loghandler ;
	public static float[] hw={188,23f};
	public static LatLng currentPosition ;
	public static Date IPS_UpdateTime = new Date();
	public static boolean IPS_flag = true;
	public static LatLng ancer = new LatLng(1.342518999,103.679474999);
	private static File sd = Environment.getExternalStorageDirectory();
	public static String path = sd.getPath() + "sensorInfo.txt";
	public static Hashtable<String, Beacon> templist = new Hashtable<String, Beacon>();
	public static Hashtable<String, Beacon> beaconlist = new Hashtable<String, Beacon>();

}
