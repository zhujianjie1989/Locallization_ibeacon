package com.iot.locallization_ibeacon.pojo;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Beacon {
	public String ID;
	public String mac="";
	public Integer rssi=-100;
	public String major;
	public String minor;
	public String UUID;
	public int TxPower;
	public int max_rssi=-50;
	public Integer floor=0;
	public LatLng position ;
	public long updateTime;
	public int type=0;
	public int pipeNum=0;
	public MarkerOptions markerOptions = new MarkerOptions();
	public HashMap<String,Beacon> neighbors = new HashMap();
	public HashMap<String,Edge> edges = new HashMap();

	public boolean isVisit = false ;

	private final  int length = 3 ;
	private int[] rssis = new int[length];
	private int pos = 0;

	public Beacon(){

		for (int i = 0 ;i < length;i++)
		{
			rssis[i]=-120;
		}
	}

	public Beacon(String ID, String UUID, String Mac, String Major, String Minor, int Rssi, int TxPower){
		this.ID= ID;
		this.UUID= UUID;
		this.mac=Mac;
		this.major  =Major;
		this.minor = Minor;
		this.rssi= Rssi;
		this.TxPower = TxPower;

	}
	public void setRssi(int rssi)
	{
		rssis[pos] = rssi;
		pos= (pos+1)%length;
		int sum=0;
		for (int i = 0 ;i < length;i++)
		{
			sum+=rssis[i];
		}
		this.rssi = sum/length;
	}

	public String toString()
	{
		return   this.ID+","+this.major+","+this.minor+","+this.rssi+","+this.position.latitude+","+ this.position.longitude+","+this.floor+","+this.max_rssi;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.neighbors = null;
		this.edges = null;
	}
}
