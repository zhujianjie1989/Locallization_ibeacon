package com.iot.locallization_ibeacon.pojo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by zhujianjie on 2015/9/21.
 */
public class Destionation {
    public String name;
    public Beacon postion;
    public Destionation(String name,Beacon beacon){
        this.name = name;
        this.postion = beacon;
    }
}
