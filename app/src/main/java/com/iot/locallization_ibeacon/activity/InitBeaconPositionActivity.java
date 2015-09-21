package com.iot.locallization_ibeacon.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.iot.locallization_ibeacon.R;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.Edge;
import com.iot.locallization_ibeacon.pojo.GlobalData;
import com.iot.locallization_ibeacon.tools.Tools;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;


public class InitBeaconPositionActivity extends ActionBarActivity {
    private GoogleMap map;



    private Hashtable<String,Beacon> markerList = new Hashtable<String,Beacon>();
    public int floor=4;
    private Marker marker;
    private int markID=0;
    private  Circle circle;
    private TimerTask task;
    private boolean addEdgeFlag =false;
    private boolean curr_or_max=true;
    private GroundOverlay image=null;
    private final Timer timer = new Timer();

    private GlobalData.EdgeAction edge_action = GlobalData.EdgeAction.NORMAL;
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

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
            if (marker !=null&&GlobalData.beaconlist!=null){
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

        Button BT_DELETE = (Button)findViewById(R.id.BT_DELETE);
        Button BT_Calibreate = (Button)findViewById(R.id.BT_Calibreate);
        Button BT_Pluse = (Button)findViewById(R.id.BT_Pluse);
        Button BT_Sub = (Button)findViewById(R.id.BT_Sub);
        RadioButton RB_Curr = (RadioButton)findViewById(R.id.RB_Curr);
        RadioButton RB_Max = (RadioButton)findViewById(R.id.RB_Max);
        RadioButton RB_Normal = (RadioButton)findViewById(R.id.RB_Normal);
        RadioButton RB_AddLine = (RadioButton)findViewById(R.id.RB_AddLine);
        RadioButton RB_Delete = (RadioButton)findViewById(R.id.RB_Delete);

        Button BT_Indoor = (Button)findViewById(R.id.BT_Indoor);
        Button BT_Outdoor = (Button)findViewById(R.id.BT_Outdoor);
        Button BT_Stairs = (Button)findViewById(R.id.BT_Stairs);
        Button BT_Elevator = (Button)findViewById(R.id.BT_Elevator);
        Button BT_SetPipe = (Button)findViewById(R.id.BT_SetPipe);
        Button BT_PipePluse = (Button)findViewById(R.id.BT_PipePluse);
        Button BT_PipeSub = (Button)findViewById(R.id.BT_PipeSub);

        BT_PipePluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView TV_PipeNum = (TextView)findViewById(R.id.TV_PipeNum);
                int num = Integer.parseInt(TV_PipeNum.getText().toString())+1;
                TV_PipeNum.setText(""+num);
            }
        });
        BT_PipeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView TV_PipeNum = (TextView)findViewById(R.id.TV_PipeNum);
                int num = Integer.parseInt(TV_PipeNum.getText().toString())-1;
                TV_PipeNum.setText(""+num);
            }
        });

        BT_SetPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InitBeaconPositionActivity.this.marker!=null)
                {

                    Beacon beacon = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                    switch (GlobalData.BeaconType.values()[beacon.type]){
                        case  STAIRS:
                        case  ELEVATOR:
                            TextView num = (TextView)findViewById(R.id.TV_PipeNum);
                            beacon.pipeNum = Integer.parseInt(num.getText().toString());
                            Tools.updateBeacon(beacon,InitBeaconPositionActivity.this);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        BT_Indoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InitBeaconPositionActivity.this.marker!=null)
                {
                    Beacon beacon = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                    beacon.type = GlobalData.BeaconType.INDOOR.ordinal();
                    Tools.updateBeacon(beacon,InitBeaconPositionActivity.this);
                }
            }
        });
        BT_Outdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InitBeaconPositionActivity.this.marker!=null) {
                    Beacon beacon = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                    beacon.type = GlobalData.BeaconType.OUTDOOR.ordinal();
                    Tools.updateBeacon(beacon, InitBeaconPositionActivity.this);
                }
            }
        });
        BT_Stairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InitBeaconPositionActivity.this.marker!=null) {
                    Beacon beacon = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                    beacon.type = GlobalData.BeaconType.STAIRS.ordinal();
                    Tools.updateBeacon(beacon, InitBeaconPositionActivity.this);
                }
            }
        });
        BT_Elevator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InitBeaconPositionActivity.this.marker!=null) {
                    Beacon beacon = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                    beacon.type = GlobalData.BeaconType.ELEVATOR.ordinal();
                    Tools.updateBeacon(beacon, InitBeaconPositionActivity.this);
                }
            }
        });

        BT_Pluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor++;
                TextView tvfloor = (TextView) findViewById(R.id.TV_Floor);
                tvfloor.setText(floor + "");
                changeImage();
            }
        });

        BT_Sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor--;
                TextView tvfloor = (TextView)findViewById(R.id.TV_Floor);
                tvfloor.setText(floor + "");
                changeImage();
            }
        });

        BT_DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null) {
                    Beacon beacon = markerList.get(marker.getTitle());
                    Tools.deleteBeacon(beacon, InitBeaconPositionActivity.this);
                    markerList.remove(marker.getTitle());
                    marker.remove();
                    marker=null;


                }
            }
        });


        BT_Calibreate.setOnClickListener(new View.OnClickListener() {
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
                    if (max_sensor == null
                            ||(max_sensor!=null
                                &&!max_sensor.ID.equals(sensor.ID)
                                &&GlobalData.beaconlist.keySet().contains(max_sensor.ID))) {
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
                    sensor.edges = new HashMap<String,Edge>();
                    sensor.neighbors =new HashMap<String,Beacon>();

                    Tools.insertBeacon(sensor, InitBeaconPositionActivity.this);
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



        RB_Curr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    curr_or_max = false;
                }
            }
        });
        RB_Max.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    curr_or_max = true;
                }
            }
        });


        RB_Normal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    edge_action = GlobalData.EdgeAction.NORMAL;
                }
            }
        });
        RB_AddLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edge_action = GlobalData.EdgeAction.ADD_LINE;
                }
            }
        });
        RB_Delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    edge_action = GlobalData.EdgeAction.DELETE_LINE;
                }
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
                if (circle != null)
                    circle.remove();
                Log.e("initMap", "onMapClick");
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if ( InitBeaconPositionActivity.this.marker!=null
                        &&marker.getTitle().endsWith( InitBeaconPositionActivity.this.marker.getTitle()))
                    return false;


                Log.e("initMap ", "addEdgeFlag= " + addEdgeFlag);
                switch (edge_action) {
                    case NORMAL:
                        InitBeaconPositionActivity.this.marker = marker;
                        marker.setSnippet("nabors: " +GlobalData.beaconlist.get(marker.getTitle()).neighbors.size()
                                +" edges: "+GlobalData.beaconlist.get(marker.getTitle()).edges.size()
                                + "type: " + +GlobalData.beaconlist.get(marker.getTitle()).type
                                + "pipeNum: " + +GlobalData.beaconlist.get(marker.getTitle()).pipeNum
                                + "\n max_rssi:" + markerList.get(marker.getTitle()).max_rssi);
                       /* marker.setSnippet("nabors count = " +GlobalData.beaconlist.get(marker.getTitle()).neighbors.size()
                                +" edges  count = "+GlobalData.beaconlist.get(marker.getTitle()).edges.size()
                                + "type = " + +GlobalData.beaconlist.get(marker.getTitle()).type);*/

                        markerList.get(marker.getTitle()).markerOptions.position(marker.getPosition());
                        markerList.get(marker.getTitle()).position = marker.getPosition();
                        return false;

                    case ADD_LINE:
                        if (InitBeaconPositionActivity.this.marker != null) {
                            Log.e("initMap ", "form = " + InitBeaconPositionActivity.this.marker.getPosition().longitude + " " +
                                    InitBeaconPositionActivity.this.marker.getPosition().latitude
                                    + marker.getPosition().latitude + " " + marker.getPosition().longitude);

                            PolylineOptions rectOptions = new PolylineOptions()
                                    .add(marker.getPosition())
                                    .add(InitBeaconPositionActivity.this.marker.getPosition()).color(Color.RED);

                            Polyline polyline = map.addPolyline(rectOptions);
                            Edge edge1 = new Edge(InitBeaconPositionActivity.this.marker.getTitle(), marker.getTitle(), polyline);
                            Edge edge2 = new Edge(marker.getTitle(), InitBeaconPositionActivity.this.marker.getTitle(), polyline);


                            Beacon from = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                            Beacon to = GlobalData.beaconlist.get(marker.getTitle());

                            if(from.edges.get(edge1.ID)==null){
                                from.edges.put(edge1.ID, edge1);
                                from.neighbors.put(to.ID, to);
                                Tools.insertEdge(edge1, InitBeaconPositionActivity.this);
                            }else {
                                polyline.remove();
                            }

                            if(to.edges.get(edge2.ID)==null){
                                to.edges.put(edge2.ID, edge2);
                                to.neighbors.put(from.ID, from);
                                Tools.insertEdge(edge2, InitBeaconPositionActivity.this);
                            }else{
                                polyline.remove();
                            }
                            Log.e("insertEdge", "----------------------------------------------");

                        }

                        InitBeaconPositionActivity.this.marker = marker;
                        if (circle != null)
                            circle.remove();

                        circle = map.addCircle(new CircleOptions().center(marker.getPosition()).radius(1.5).strokeWidth(15).strokeColor(Color.GREEN));
                        marker.hideInfoWindow();
                        return true;

                    case DELETE_LINE:
                        if (InitBeaconPositionActivity.this.marker != null) {
                            Log.e("initMap ", "form = " + InitBeaconPositionActivity.this.marker.getPosition().longitude + " " +
                                    InitBeaconPositionActivity.this.marker.getPosition().latitude
                                    + marker.getPosition().latitude + " " + marker.getPosition().longitude);

                            Beacon from = GlobalData.beaconlist.get(InitBeaconPositionActivity.this.marker.getTitle());
                            Beacon to = GlobalData.beaconlist.get(marker.getTitle());

                            if (from!=null)
                            {
                                Edge edge1 = GlobalData.beaconlist.get(from.ID).edges.get(from.ID + to.ID);
                                if (edge1!=null){
                                    edge1.polyline.remove();
                                    from.edges.remove(edge1.ID);
                                    from.neighbors.remove(to.ID);
                                    Tools.deleteEdge(edge1, InitBeaconPositionActivity.this);
                                }
                            }

                            if (to!=null){
                                Edge edge2 = GlobalData.beaconlist.get(to.ID).edges.get(to.ID + from.ID);

                                if (edge2!=null){
                                    edge2.polyline.remove();
                                    to.edges.remove(edge2.ID);
                                    to.neighbors.remove(from.ID);
                                    Tools.deleteEdge(edge2, InitBeaconPositionActivity.this);
                                }
                            }


                            Log.e("deleteEdge", "----------------------------------------------");


                        }

                        InitBeaconPositionActivity.this.marker = marker;
                        if (circle != null)
                            circle.remove();

                        circle = map.addCircle(new CircleOptions().center(marker.getPosition()).radius(1.5).strokeWidth(15).strokeColor(Color.GREEN));
                        marker.hideInfoWindow();
                        return true;
                    default:
                        break;
                    }
                return  false;

                }

        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Beacon sensor = new Beacon();
                sensor.markerOptions = new MarkerOptions().position(latLng)
                        .draggable(true).title("111"+ markID)
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

                Tools.insertBeacon(sensor,InitBeaconPositionActivity.this);



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
                map.addMarker(sensor.markerOptions).setDraggable(true);
            }

        }

        List<Edge> edges = Tools.getAllEdge(this);
        for(int index = 0 ; index < edges.size() ; index++){
            Edge edge = edges.get(index);


            Beacon from = GlobalData.beaconlist.get(edge.ID_From);
            Beacon to = GlobalData.beaconlist.get(edge.ID_To);
            from.neighbors.put(to.ID,to);
            from.edges.put(edge.ID,edge);

            edge.polyline = map.addPolyline(new PolylineOptions()
                    .add(from.position)
                    .add(to.position).color(Color.RED));

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

