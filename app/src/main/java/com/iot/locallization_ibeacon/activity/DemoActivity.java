package com.iot.locallization_ibeacon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Notifications;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.iot.locallization_ibeacon.R;
import com.iot.locallization_ibeacon.algorithm.WPL_Limit_BlutoothLocationAlgorithm;
import com.iot.locallization_ibeacon.navigation.Navigation;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.Destionation;
import com.iot.locallization_ibeacon.pojo.Edge;
import com.iot.locallization_ibeacon.pojo.GlobalData;
import com.iot.locallization_ibeacon.pojo.Node;
import com.iot.locallization_ibeacon.tools.Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DemoActivity extends Activity {
    private GoogleMap map;
    private Marker currmark=null;
    public static  String logstring ="";
    private Handler updateHandler = new Handler();
    private GroundOverlay buildingMapImage =null;
    private Location currentLocation =null;
    private LocationManager locationManager;
    private WPL_Limit_BlutoothLocationAlgorithm location =new WPL_Limit_BlutoothLocationAlgorithm();
    Destionation destinaton ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        GlobalData.loghandler = updatelog ;//如果楼层改变loghandler用于改变地图

        //-------------------------------------------------------------------
        //下面两步顺序不能改变
        initMap();  //初始化地图
        readConf(); //读取配置文件
        //--------------------------------------------------------------------

        changeBuildingMap();    //显示当前楼层地图
        initUI();

    }


    private void initUI(){
        Spinner spiner = (Spinner)findViewById(R.id.spinner);

        Destionation d1 = new Destionation("Chair of EEE", GlobalData.beaconlist.get("1128"));
        Destionation d2 = new Destionation("Men Toilet", GlobalData.beaconlist.get("1121"));
        Destionation d3 = new Destionation("Prof Er Office", GlobalData.beaconlist.get("1101"));
        Destionation d4 = new Destionation("IOT Lab", GlobalData.beaconlist.get("1412"));
        Destionation d5= new Destionation("Robotic Lab", GlobalData.beaconlist.get("1440"));

        final List<Destionation> destionList = new ArrayList<Destionation>();
        destionList.add(d1);
        destionList.add(d2);
        destionList.add(d3);
        destionList.add(d4);
        destionList.add(d5);

        destinaton = destionList.get(0);
        List<String> dataList = new ArrayList<String>();
        for (int i = 0 ; i < destionList.size() ; i++){
            dataList.add(destionList.get(i).name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,dataList);
        spiner.setAdapter(adapter);
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("onItemSelected", destionList.get(i).name);
                destinaton = destionList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button start = (Button)findViewById(R.id.BT_SatrtNavigation);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation nv = new Navigation();
                nv.startFindPath(GlobalData.beaconlist.get("146"),GlobalData.beaconlist.get("1421"));
            }
        });


    }
    /**
     * 改变楼层地图
     */
    private void changeBuildingMap() {
        BitmapDescriptor img =null;
        Log.e("changeBuildingMap", " floor = " + GlobalData.curr_floor);
        switch(GlobalData.curr_floor)
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
        buildingMapImage.remove();
        buildingMapImage = map.addGroundOverlay(new GroundOverlayOptions()
                .image(img).anchor(0, 0).bearing(-45f)
                .position(GlobalData.ancer, GlobalData.hw[0], GlobalData.hw[1]));
    }

    /**
     * 更新地图
     */
    private void updateMap() {
        Date date = new Date();
        if (Math.abs(date.getTime() - GlobalData.IPS_UpdateTime.getTime()) >  6000)
        {
            openGPS();
            return;
        }

        if(locationManager!=null ){
            locationManager.removeUpdates(GPSlistener);
            locationManager = null;
        }

        location.setHandler(updatelog);
        location.DoLocalization();
        updateLocation(GlobalData.currentPosition);

        cleanScanbeaconlist();

    }

    private  void cleanScanbeaconlist(){
        Date now = new Date();


            Iterator<String> iter =GlobalData.scanbeaconlist.keySet().iterator();  //用一个时间段内扫描到的beacon计算
            Log.e("cleanScanbeaconlist","cleanScanbeaconlist start size = "+GlobalData.scanbeaconlist.size());
            List<Beacon> removeList = new ArrayList<>();
            while (iter.hasNext()) {
                String key =  iter.next();
                Beacon sensor =GlobalData.scanbeaconlist.get(key);
                Log.e("out   ============", "time = " + (now.getTime() - sensor.updateTime) );
                if (now.getTime() - sensor.updateTime > 2000)
                {
                    Log.e("int   ============", "time = " + (now.getTime() - sensor.updateTime) );
                    removeList.add(sensor);
                }
            }


          for (int index = 0 ; index < removeList.size();index++){
                Beacon beacon = removeList.get(index);
                GlobalData.scanbeaconlist.remove(beacon.ID);
            }

            removeList =null;
        Log.e("cleanScanbeaconlist","cleanScanbeaconlist end size = "+GlobalData.scanbeaconlist.size());



    }

    /**
     * 更新位置
     * @param location
     */
    public void updateLocation(LatLng location){
        if (currmark!= null)
        {
            currmark.remove();
        }
        currmark=map.addMarker(new MarkerOptions().position(location));

       // currmark=map.addMarker(new MarkerOptions().position(GlobalData.currentPosition));
    }



    /**
     * 初始化GPS设置
     */
    private void openGPS() {

        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 0, GPSlistener);
            return;

        }

        Toast.makeText(this, "GPS dont open", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivityForResult(intent, 0);


    }

    /**
     * 初始化地图
     */
    private void  initMap() {

        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setIndoorEnabled(true);
        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setIndoorLevelPickerEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        buildingMapImage = map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.k44)).anchor(0,0).bearing(-45f)
                .position(GlobalData.ancer,GlobalData.hw[0],GlobalData.hw[1]));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(GlobalData.ancer, 23);
        map.moveCamera(update);

    }


    private void readConf(){

        Tools.ReadConfigFile(this);
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
        updateHandler.postDelayed(updateMap, 1000);//开始更新地图

    }


    LocationListener GPSlistener =  new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if(currentLocation!=null){
                if(Tools.isBetterLocation(location, currentLocation)){
                    Log.v("GPSTEST", "It's a better location");
                    currentLocation=location;
                    updateLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                }
                else{
                    Log.v("GPSTEST", "Not very good!");
                }
            }
            else if(location.getAccuracy() < 5)
            {
                Log.v("GPSTEST", "It's first location");
                currentLocation=location;
                updateLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private Runnable updateMap = new Runnable()
    {
        @Override
        public void run()
        {
            updateMap();
            updateHandler.postDelayed(updateMap, 1000);
        }
    };

    Handler updatelog = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.arg1 == 2)
            {
                changeBuildingMap();
            }
            super.handleMessage(msg);

        }
    };
}
