package com.iot.locallization_ibeacon.pojo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by zhujianjie on 10/7/2015.
 */
public class Node {
    private int ID;
    private int counter;
    private LatLng latLng;
    public Node(int id,int counter,LatLng latLng){
        this.ID =id;
        this.counter = counter;
        this.latLng = latLng;
    }
    public String toString(){
        return this.ID +" "+this.counter+"   "+this.latLng.latitude+"   "+this.latLng.longitude;
    }

    private  double formate(double num){
        return  ((double)(int)(num*100000000))/100000000;
    }
}
