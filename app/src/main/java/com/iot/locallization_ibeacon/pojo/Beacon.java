package com.iot.locallization_ibeacon.pojo;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Beacon {
	public String ID;
	public String mac="";
	public Integer rssi=-100;
	public String major;
	public String minor;
	public String Name;
	public String UUID;
	public int TxPower;
	public int max_rssi=-50;
	public Integer floor=0;
	public LatLng position ;
	public long updateTime;
	public MarkerOptions markerOptions = new MarkerOptions();


	private final  int length =2 ;
	public int[] rssis = new int[length];
	public int pos = 0;

	public Beacon(){

		for (int i = 0 ;i < length;i++)
		{
			rssis[i]=-150;
		}
	}

	public Beacon(String Name, String UUID, String Mac, String Major, String Minor, int Rssi, int TxPower){
		this.Name= Name;
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

}
