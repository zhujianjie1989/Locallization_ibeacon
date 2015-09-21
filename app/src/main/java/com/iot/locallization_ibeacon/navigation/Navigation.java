package com.iot.locallization_ibeacon.navigation;

import android.util.Log;

import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.GlobalData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhujianjie on 2015/9/21.
 */
public class Navigation {

    int startFloor = 0;
    int endFloor = 0;

    public void startFindPath(Beacon startBeacon,Beacon endBeacon){
        startFloor = startBeacon.floor;
        endFloor = endBeacon.floor;

        if (startFloor == endFloor){
            Log.e("================>","startFindPath");
            findSameFloorPath(endFloor, 0, startBeacon, endBeacon);
            Log.e("================>", "endFindPath");
        }else{

        }

    }

    List<Beacon> current = new ArrayList<>();
    List<Beacon> best = new ArrayList<>();
    int shortestLenth = 10000000;

    public void  findSameFloorPath(int floor,int cost,Beacon startBeacon,Beacon endBeacon){


        cost++;
        current.add(startBeacon);
        if (startBeacon.ID.equals(endBeacon.ID))
        {
            if (shortestLenth > cost)
            {
                shortestLenth = cost;

                best = null;
                best = new ArrayList<>();
                best.addAll(current);

                String path="";
                for (int i = 0 ; i <best.size();i++){
                    path +=" "+best.get(i).ID;
                }
                Log.e("best Path ",path);

            }else{
                current.remove(startBeacon);
            }

        }

        startBeacon.isVisit =true;
       // boolean flag = false;
        List<Beacon> floorNodes = findSameFloorNode(floor);
        Iterator<String> keytie = startBeacon.neighbors.keySet().iterator();

        while(keytie.hasNext()){
            String key = keytie.next();
            Beacon beacon = GlobalData.beaconlist.get(key);
            if (!beacon.isVisit){
                findSameFloorPath(floor,cost, beacon, endBeacon);
            }

        }

        startBeacon.isVisit = false;
        current.remove(startBeacon);
    }

    public  List<Beacon> findSameFloorNode(int floor){
        List<Beacon> beaconlist = new ArrayList<Beacon>();
        Iterator<String> keytie =   GlobalData.beaconlist.keySet().iterator();
        while(keytie.hasNext()){
            String key = keytie.next();
            Beacon beacon = GlobalData.beaconlist.get(key);
            if (beacon.floor == floor)
            {
                beaconlist.add(beacon);
            }
        }

        return beaconlist;
    }
}
