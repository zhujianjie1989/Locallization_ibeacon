package com.iot.locallization_ibeacon.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.iot.locallization_ibeacon.pojo.Beacon;

import java.util.ArrayList;
import java.util.List;

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
        sqLiteDatabase.execSQL("create table device(" +
                "id     varchar(20)," +
                "major  varchar(20)," +
                "minor  varchar(20)," +
                "uuid   varchar(20)," +
                "lat    varchar(20)," +
                "lng    varchar(20)," +
                "floor  varchar(20)," +
                "rssi   varchar(20)," +
                "type   varchar(20))");
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
            sqLiteDatabase.insert("device",null,values);
        }


    }

    public void insert(Beacon beacon){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

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

        sqLiteDatabase.insert("device",null,values);
    }

    public void delete(Beacon beacon){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] args = {beacon.ID};

        sqLiteDatabase.delete("device", "ID=?", args);
    }

    public void update(Beacon beacon){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

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
        String[] args = {beacon.ID};

        sqLiteDatabase.update("device", values, "ID=?", args);
    }

    public void findByID(String ID){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] args = {ID};
        Cursor cursor =  sqLiteDatabase.query("device",null,"ID=?",args,null,null,null,null);
        while(cursor.moveToNext()){
            Beacon beacon = new Beacon();
            beacon.ID = cursor.getString(0);
            beacon.major = cursor.getString(1);
            beacon.minor  =cursor.getString(2);
            beacon.position = new LatLng(Double.parseDouble(cursor.getString(4))
                    ,Double.parseDouble(cursor.getString(5)));
            beacon.floor= Integer.parseInt(cursor.getString(6));
            beacon.max_rssi  = Integer.parseInt(cursor.getString(7));
            beacon.type  = Integer.parseInt(cursor.getString(8));


            Log.e("findByID",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "
                    +cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "
                    +cursor.getString(6) + " " +cursor.getString(7) + " " +cursor.getString(8));
        }

    }

    public List<Beacon> selectAll(){
        List<Beacon> beacons = new ArrayList<Beacon>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor =  sqLiteDatabase.query("device",null,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            Beacon beacon = new Beacon();
            beacon.ID = cursor.getString(0);
            beacon.major = cursor.getString(1);
            beacon.minor  =cursor.getString(2);
            beacon.position = new LatLng(Double.parseDouble(cursor.getString(4))
                                        ,Double.parseDouble(cursor.getString(5)));
            beacon.floor= Integer.parseInt(cursor.getString(6));
            beacon.max_rssi  = Integer.parseInt(cursor.getString(7));
            beacon.type  = Integer.parseInt(cursor.getString(8));

            beacons.add(beacon);
           /* Log.e("SQLiteHelper",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "
                    +cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "
                    +cursor.getString(6) + " " +cursor.getString(7) + " " +cursor.getString(8));*/
        }

        return beacons;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
