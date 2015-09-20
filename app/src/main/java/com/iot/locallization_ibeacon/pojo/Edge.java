package com.iot.locallization_ibeacon.pojo;

import android.webkit.WebSettings;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by zhujianjie on 2015/9/20.
 */
public class Edge {
    public String ID;
    public String ID_From;
    public String ID_To;
    public Polyline polyline;
    public Edge(String from ,String to ,Polyline polyline){
        this.ID = from+to;
        this.ID_From = from;
        this.ID_To = to;
        this.polyline = polyline;
    }

    public Edge(){}

    public String toString(){
        return  ID+"  "+ID_From+"  "+ID_To;
    }
}
