package com.iot.locallization_ibeacon.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iot.locallization_ibeacon.R;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.GlobalData;
import com.iot.locallization_ibeacon.tools.Tools;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class InitBeaconPositionActivity extends ActionBarActivity {
    private GoogleMap map;
    private Hashtable<String,Beacon> markerList = new Hashtable<String,Beacon>();
    private Marker marker;
    private int markID=0;
    private final Timer timer = new Timer();
    private TimerTask task;
    private boolean addLine_flag=false;
    private boolean curr_or_max=true;
    private GroundOverlay image=null;
    public int floor=4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initbeaconposition);
        initButton();
        initMap();

        getSupportActionBar().hide();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();

                message.what = 1;
                Loghandler.sendMessage(message);
            }
        };
        timer.schedule(task, 500, 500);
        GlobalData.loghandler = Loghandler;
    }

    public String getID(String ma ,String mi){
        return "major:" +  ma + " minor:" +  mi;
    }

    Handler Loghandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1){
                TextView log3 = (TextView) findViewById( R.id.TV_Log3);
                log3.setText(GlobalData.log);
            }

            TextView log1 = (TextView) findViewById( R.id.TV_Log1);
            TextView log2 = (TextView) findViewById( R.id.TV_Log2);
            if (marker !=null){
                Beacon sensor =  GlobalData.beaconlist.get(marker.getTitle());
                if (sensor!=null)
                {
                    Beacon max_sensor = Tools.getSensorByMajorandMinor(sensor.major,sensor.minor);
                    if (max_sensor==null)
                        return;
                    log1.setText("cur_major:" + max_sensor.major + " cur_minor:" + max_sensor.minor + " rssi:" + max_sensor.rssi);
                }

            }



            Beacon max_sensor = Tools.getMaxRssiSensor(GlobalData.templist);
            if (max_sensor==null)
                return;
            log2.setText("max_major:" + max_sensor.major + " max_minor:" + max_sensor.minor + " rssi:" + max_sensor.rssi);



            super.handleMessage(msg);
        }
    };


    private void initButton(){

        Button delete = (Button)findViewById(R.id.BT_DELETE);
        Button calibrate = (Button)findViewById(R.id.BT_Calibreate);
        Button pluse = (Button)findViewById(R.id.BT_Pluse);
        Button sub = (Button)findViewById(R.id.BT_Sub);

        pluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor++;
                TextView tvfloor = (TextView)findViewById(R.id.TV_Floor);
                tvfloor.setText(floor+"");
                changeImage();
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor--;
                TextView tvfloor = (TextView)findViewById(R.id.TV_Floor);
                tvfloor.setText(floor + "");
                changeImage();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker !=null){
                    Tools.deleteBeacon(markerList.get(marker.getTitle()),InitBeaconPositionActivity.this);
                    markerList.remove(marker.getTitle());
                    marker.remove();
                }
            }
        });


        calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView log = (TextView) findViewById(R.id.TV_Log1);
                if (marker != null && curr_or_max == false) {
                    //把选定的beacon更新其信号最强值

                    Beacon sensor = GlobalData.beaconlist.get(marker.getTitle());
                    sensor.max_rssi = sensor.rssi;
                    sensor.markerOptions.snippet("x:" + Tools.formatFloat(sensor.position.latitude)
                            + " y:" + Tools.formatFloat(sensor.position.longitude) + "\n"
                            + "max_rssi:" + sensor.max_rssi);
                    marker.remove();
                    marker = map.addMarker(sensor.markerOptions);
                    marker.showInfoWindow();

                    Tools.updateBeacon(sensor,InitBeaconPositionActivity.this);

                }
                else if (marker != null && curr_or_max == true) {
                    //把选定的beacon更新为信号最强的beacon
                    Beacon sensor = GlobalData.beaconlist.get(marker.getTitle());

                    Beacon max_sensor = Tools.getMaxRssiSensor(GlobalData.templist);
                    if (max_sensor == null) {
                        return;
                    }

                    log.setText("major:" + max_sensor.major + " minor:" + max_sensor.minor + " rssi:" + max_sensor.rssi);

                    GlobalData.beaconlist.remove(sensor.ID);
                    Tools.deleteBeacon(sensor, InitBeaconPositionActivity.this);

                    sensor.ID =  max_sensor.major + max_sensor.minor;
                    sensor.major = max_sensor.major;
                    sensor.minor = max_sensor.minor;
                    sensor.UUID = max_sensor.UUID;
                    sensor.max_rssi = max_sensor.rssi;
                    sensor.markerOptions.title(sensor.ID);

                    Tools.insertBeacon(sensor,InitBeaconPositionActivity.this);
                    GlobalData.beaconlist.put(sensor.ID, sensor);

                    sensor.markerOptions.title(sensor.ID);
                    sensor.markerOptions.snippet("x:" + Tools.formatFloat(sensor.position.latitude) + " y:" + Tools.formatFloat(sensor.position.longitude) + "\n"
                            + "max_rssi:" + sensor.max_rssi);

                    marker.remove();
                    marker = map.addMarker(sensor.markerOptions);
                    marker.showInfoWindow();

                }

            }
        });


        RadioButton curr = (RadioButton)findViewById(R.id.RB_Curr);
        RadioButton max = (RadioButton)findViewById(R.id.RB_Max);
        curr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                curr_or_max = false;
            }
        });
        max.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                curr_or_max = true;
            }
        });

    }

    private void initMap(){
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
       // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker = null;
                Log.e("initMap", "onMapClick");
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                InitBeaconPositionActivity.this.marker = marker;
                marker.setSnippet("x:" + Tools.formatFloat(marker.getPosition().latitude)
                        + " y:" + Tools.formatFloat(marker.getPosition().longitude)
                        + "\n max_rssi:" + markerList.get(marker.getTitle()).max_rssi);
                markerList.get(marker.getTitle()).markerOptions.position(marker.getPosition());
                markerList.get(marker.getTitle()).position = marker.getPosition();
                return false;
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Beacon sensor = new Beacon();
                sensor.markerOptions = new MarkerOptions().position(latLng)
                        .draggable(true).title(getID("111", markID + ""))
                        .snippet("x:" + Tools.formatFloat(latLng.latitude)
                                + " y:" + Tools.formatFloat(latLng.longitude) + "\n"
                                + "max_rssi:" + sensor.max_rssi);

                sensor.ID = sensor.markerOptions.getTitle();
                sensor.position = latLng;
                sensor.major = "111";
                sensor.minor = markID + "";
                sensor.floor = floor;
                markID++;

                map.addMarker(sensor.markerOptions);
                markerList.put(sensor.ID, sensor);


            }
        });


        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                marker.setSnippet("x:" + Tools.formatFloat(marker.getPosition().latitude)
                        + " y:" + Tools.formatFloat(marker.getPosition().longitude)
                        + "\n max_rssi:" + markerList.get(marker.getTitle()).max_rssi);
                markerList.get(marker.getTitle()).markerOptions.position(marker.getPosition());
                markerList.get(marker.getTitle()).position = marker.getPosition();

            }
        });


        image  =  map.addGroundOverlay( new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.k44)).anchor(0,0).bearing(-45f)
                        .position(GlobalData.ancer, GlobalData.hw[0], GlobalData.hw[1]));


        Tools.ReadConfigFile(InitBeaconPositionActivity.this);
        markerList=  GlobalData.beaconlist;

        Iterator<String> ita= markerList.keySet().iterator();
        while(ita.hasNext())
        {
            Beacon sensor = markerList.get(ita.next());
            if (sensor.floor == floor)
            {
                map.addMarker(sensor.markerOptions);
            }

        }


        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(GlobalData.ancer, 22);
        map.moveCamera(update);
    }
    private void changeImage(){



        BitmapDescriptor img =null;
        switch(floor)
        {
            case 1:
                img=BitmapDescriptorFactory.fromResource(R.drawable.k11);
                break;
            case 2:
                img=BitmapDescriptorFactory.fromResource(R.drawable.k22);
                break;
            case 3:
                img=BitmapDescriptorFactory.fromResource(R.drawable.k33);
                break;
            case 4:
                img=BitmapDescriptorFactory.fromResource(R.drawable.k44);
                break;
            default:
                return;

        }
        map.clear();
        if (image != null){

            image = map.addGroundOverlay(new GroundOverlayOptions()
                    .image(img).anchor(0, 0).bearing(-45f)
                    .position(GlobalData.ancer, GlobalData.hw[0], GlobalData.hw[1]));
        }

        Iterator<String> key_ite = markerList.keySet().iterator();

        while(key_ite.hasNext()){
            Beacon sensor = markerList.get(key_ite.next());
            if (sensor.floor ==floor){

                map.addMarker(sensor.markerOptions);
            }


        }

    }
}

