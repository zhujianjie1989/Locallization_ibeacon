package com.iot.locallization_ibeacon.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.Edge;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by zhujianjie on 2015/9/19.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper(Context context, String name, int version){
        this(context, name, null, version);
    }

    public SQLiteHelper(Context context, String name){

        this(context, name, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.createDeviceTable(sqLiteDatabase);
        this.createEdgeTable(sqLiteDatabase);
    }

    public void createDeviceTable(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("create table device(" +
                "id     varchar(20)," +
                "major  varchar(20)," +
                "minor  varchar(20)," +
                "uuid   varchar(20)," +
                "lat    varchar(20)," +
                "lng    varchar(20)," +
                "floor  varchar(20)," +
                "rssi   varchar(20)," +
                "type   varchar(20)," +
                "pipeNum   varchar(20))");
        List<Beacon> beacons = Tools.ReadConfigFile2();
        for (Beacon beacon: beacons) {
            ContentValues values = new ContentValues();
            values.put("id",beacon.major + beacon.minor);
            values.put("major",beacon.major);
            values.put("minor",beacon.minor);
            values.put("uuid","74278BDA-B644-4520-8F0C-720EAF059935");
            values.put("lat",beacon.position.latitude);
            values.put("lng",beacon.position.longitude);
            values.put("floor",beacon.floor);
            values.put("rssi",beacon.max_rssi);
            values.put("type","0");
            values.put("pipeNum","0");
            sqLiteDatabase.insert("device",null,values);
        }
    }

    public void insertBeacon(Beacon beacon){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id",beacon.major + beacon.minor);
        values.put("major",beacon.major);
        values.put("minor",beacon.minor);
        values.put("uuid","74278BDA-B644-4520-8F0C-720EAF059935");
        values.put("lat",beacon.position.latitude);
        values.put("lng",beacon.position.longitude);
        values.put("floor",beacon.floor);
        values.put("rssi",beacon.max_rssi);
        values.put("type", beacon.type + "");
        values.put("pipeNum", beacon.pipeNum + "");
        sqLiteDatabase.insert("device", null, values);
    }

    public void deleteBeacon(Beacon beacon){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] args = {beacon.ID};

        sqLiteDatabase.delete("device", "id=?", args);
    }

    public void updateBeacon(Beacon beacon){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id",beacon.major + beacon.minor);
        values.put("major",beacon.major);
        values.put("minor",beacon.minor);
        values.put("uuid","74278BDA-B644-4520-8F0C-720EAF059935");
        values.put("lat",beacon.position.latitude);
        values.put("lng",beacon.position.longitude);
        values.put("floor", beacon.floor);
        values.put("rssi", beacon.max_rssi);
        values.put("type", beacon.type + "");
        values.put("pipeNum", beacon.pipeNum + "");
        String[] args = {beacon.ID};

        sqLiteDatabase.update("device", values, "id=?", args);
    }

    public void findBeaconByID(String ID){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] args = {ID};
        Cursor cursor =  sqLiteDatabase.query("device",null,"id=?",args,null,null,null,null);
        while(cursor.moveToNext()){
            Beacon beacon = new Beacon();
            beacon.ID = cursor.getString(0);
            beacon.major = cursor.getString(1);
            beacon.minor  =cursor.getString(2);
            beacon.UUID  = cursor.getString(3);
            beacon.position = new LatLng(Double.parseDouble(cursor.getString(4)),
                                        Double.parseDouble(cursor.getString(5)));
            beacon.floor= Integer.parseInt(cursor.getString(6));
            beacon.max_rssi  = Integer.parseInt(cursor.getString(7));
            beacon.type  = Integer.parseInt(cursor.getString(8));
            beacon.pipeNum  = Integer.parseInt(cursor.getString(9));

            Log.e("findByID",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "
                    +cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "
                    +cursor.getString(6) + " " +cursor.getString(7) + " " +cursor.getString(8)+ " " +cursor.getString(9));
        }

    }

    public List<Beacon> selectAllBeacon(){
        List<Beacon> beacons = new ArrayList<Beacon>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor =  sqLiteDatabase.query("device",null,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            Beacon beacon = new Beacon();
            beacon.ID = cursor.getString(0);
            beacon.major = cursor.getString(1);
            beacon.minor = cursor.getString(2);
            beacon.UUID  = cursor.getString(3);
            beacon.position = new LatLng(Double.parseDouble(cursor.getString(4))
                                        ,Double.parseDouble(cursor.getString(5)));
            beacon.floor= Integer.parseInt(cursor.getString(6));
            beacon.max_rssi  = Integer.parseInt(cursor.getString(7));
            beacon.type  = Integer.parseInt(cursor.getString(8));
            beacon.pipeNum  = Integer.parseInt(cursor.getString(9));
            beacons.add(beacon);
           /* Log.e("SQLiteHelper",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "
                    +cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "
                    +cursor.getString(6) + " " +cursor.getString(7) + " " +cursor.getString(8));*/
        }

        return beacons;
    }

    //====================================================================================================================
    public void createEdgeTable(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("create table edge("
                + "id  varchar(20),"
                + "id_from  varchar(20),"
                + "id_to    varchar(20))");
    }
    public void insertEdge(Edge edge){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id",edge.ID);
        values.put("id_from",edge.ID_From);
        values.put("id_to",edge.ID_To);

        sqLiteDatabase.insert("edge", null, values);
    }

    public void deleteEdge(Edge edge){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] args = {edge.ID};
        sqLiteDatabase.delete("edge", "id=?", args);
    }

    public void cleanEdge(String  ID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] args = {ID,ID};
        sqLiteDatabase.delete("edge", "id_from=? or id_to=?", args);
    }

    public void updateEdge(Edge edge){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id",edge.ID);
        values.put("id_from",edge.ID_From);
        values.put("id_to",edge.ID_To);

        String[] args = {edge.ID};
        sqLiteDatabase.update("edge", values, "id=?", args);
    }

    public List<Edge> findEdgeByID(String ID){

        List<Edge> edges = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] args = {ID};
        Cursor cursor =  sqLiteDatabase.query("edge",null,"id=?",args,null,null,null,null);
        while(cursor.moveToNext()){
            Edge edge = new Edge();
            edge.ID = cursor.getString(0);
            edge.ID_From = cursor.getString(1);
            edge.ID_To = cursor.getString(2);
            edges.add(edge);
            Log.e("selectAllEdge",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2));
        }
        return edges;
    }

    public List<Edge> selectAllEdge(){
        List<Edge> edges = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor =  sqLiteDatabase.query("edge",null,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            Edge edge = new Edge();
            edge.ID = cursor.getString(0);
            edge.ID_From = cursor.getString(1);
            edge.ID_To = cursor.getString(2);
            edges.add(edge);
            Log.e("selectAllEdge",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2));
        }

        return edges;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
