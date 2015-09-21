package com.iot.locallization_ibeacon.tools;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.Edge;
import com.iot.locallization_ibeacon.pojo.GlobalData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class Tools extends  Activity {

    public static  String path = "/sdcard/sensorInfo.txt";
    public static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static final int CHECK_INTERVAL = 1000 * 30;
    public  static boolean isBetterLocation(Location location,Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location,
        // use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must
            // be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static void ReadConfigFile(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        List<Beacon> beacons = helper.selectAllBeacon();
        for (Beacon beacon:beacons) {
            beacon.markerOptions.position(beacon.position).title(beacon.ID)
                    .snippet("x:" + Tools.formatFloat(beacon.position.latitude)
                    + " y:" + Tools.formatFloat(beacon.position.longitude)
                    + "\n max_rssi:" +beacon.max_rssi);
            GlobalData.beaconlist.put(beacon.ID,beacon);
        }

    }



    public static List<Beacon> ReadConfigFile2() {
        List<Beacon> beacons = new ArrayList();
        try {

            BufferedReader br = new BufferedReader(new FileReader(path));
            String data = br.readLine();
            GlobalData.beaconlist.clear();
            while( data!=null) {
                Beacon sensor = new Beacon();
                String[] info = data.split(",");

                sensor.ID = info[0];
                sensor.major= info[1];
                sensor.minor= info[2];
                sensor.position = new LatLng(Double.parseDouble(info[3]),Double.parseDouble(info[4]));
                sensor.floor =Integer.parseInt(info[5]);
                sensor.max_rssi = Integer.parseInt(info[6]);

                sensor.markerOptions.title(sensor.ID).draggable(true);
                sensor.markerOptions.position(sensor.position);
                sensor.markerOptions.snippet("x:" + sensor.position.latitude
                                            + "y:" + sensor.position.latitude
                                            + "\n max_rssi:" + sensor.max_rssi);
                beacons.add(sensor);
                Log.e("ReadConfigFile ", sensor.toString());
                data = br.readLine();

            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return  beacons;

    }

    public static String formatFloat(double num) {
        DecimalFormat decimalFormat=new DecimalFormat(".00000");
        String p=decimalFormat.format(num);
        return  p;
    }

    public static  String  direction(LatLng src ,LatLng dist){
        HttpOperationUtils httpOperationUtils = new HttpOperationUtils();
        String url = "http://maps.google.com/maps/api/directions/xml?";
        String param = "origin=" + src.latitude + "," + src.longitude + "&destination=" +dist.latitude
                + "," +dist.longitude + "&sensor=false&mode=walking";
        Log.e("dddd", url + param);
        return  httpOperationUtils .doGet(url + param);
    }

    public static Beacon dealScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        int startByte = 2;
        while (startByte <= 5)
        {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02
                    && ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
            {
                break;
            }
            startByte++;
        }

        byte[] uuidBytes = new byte[16];
        System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
        String hexString = bytesToHex(uuidBytes);

        String uuid = hexString.substring( 0,  8) + "-"
                    + hexString.substring( 8, 12) + "-"
                    + hexString.substring(12, 16) + "-"
                    + hexString.substring(16, 20) + "-"
                    + hexString.substring(20, 32);

        int major = (scanRecord[startByte + 20] & 0xff)
                * 0x100 + (scanRecord[startByte + 21] & 0xff);

        int minor = (scanRecord[startByte + 22] & 0xff)
                * 0x100 + (scanRecord[startByte + 23] & 0xff);

        int txPower = (scanRecord[startByte + 24]);
        String mac = device.getAddress();

        Beacon beacon = new Beacon(major+""+minor, uuid,mac,major+"",minor+"",rssi,txPower);

        return  beacon;

    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static Beacon getSensorByMajorandMinor(String major ,String minor){
        return  GlobalData.beaconlist.get(major+minor);
    }

    public static Beacon getMaxRssiSensor(Hashtable<String, Beacon> list) {
        Beacon max_sensor=null;
        int max_rssi=-10000;

        if (list==null )
            return  null;

        Iterator<String> keyite =  list.keySet().iterator();
        while (keyite.hasNext())
        {
            String key = keyite.next();
            Beacon sensor = list.get(key);
            if (sensor.rssi > max_rssi)
            {
                max_rssi = sensor.rssi;
                max_sensor = sensor;
            }
        }
        return  max_sensor;
    }

    public static void initDatabase(Context context){
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        //helper.selectAllBeacon();
    }

    public static void insertBeacon(Beacon sensor,Context context) {
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.insertBeacon(sensor);
    }

    public static void updateBeacon(Beacon sensor,Context context) {
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.updateBeacon(sensor);
    }

    public static void deleteBeacon(Beacon sensor,Context context) {
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.deleteBeacon(sensor);

        Iterator<String> keyite=   sensor.edges.keySet().iterator();
        while (keyite.hasNext()){
            String key = keyite.next();
            Edge edge = sensor.edges.get(key);
            edge.polyline.remove();
            Beacon to = GlobalData.beaconlist.get(edge.ID_To);
            to.edges.remove(to.ID + sensor.ID);
        }
        Tools.cleanEdge(sensor.ID, context);
        sensor.edges = null;
        sensor.neighbors = null;

    }



    public static void insertEdge(Edge edge,Context context) {
      //  Log.e("insertEdge", "id_from = " + edge.ID_From + " id_to = " + edge.ID_To);
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.insertEdge(edge);
      //  Log.e("insertEdge", "edge cout = " +coutEdge(context));
    }

    public static void updateEdge(Edge edge,Context context) {
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.updateEdge(edge);
    }

    public static void deleteEdge(Edge edge,Context context) {
       // Log.w("deleteEdge", "id_from = " + edge.ID_From + " id_to = " + edge.ID_To);
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.deleteEdge(edge);
       // Log.w("deleteEdge", "edge cout = " + coutEdge(context));
    }

    public static void cleanEdge(String ID,Context context) {
       // Log.w("cleanEdge", "id  " + ID);
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        helper.cleanEdge(ID);
       // Log.w("cleanEdge", "edge cout = " + coutEdge(context));
    }

    public static List<Edge> getAllEdge(Context context){
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        return  helper.selectAllEdge();

    }

    public static int coutEdge(Context context){
        DatabaseContext dbContext = new DatabaseContext(context);
        SQLiteHelper helper = new SQLiteHelper(dbContext,"BLEdevice.db");
        return helper.selectAllEdge().size();
    }

}
