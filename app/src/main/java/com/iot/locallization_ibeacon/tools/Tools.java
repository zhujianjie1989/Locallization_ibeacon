package com.iot.locallization_ibeacon.tools;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.iot.locallization_ibeacon.pojo.Beacon;
import com.iot.locallization_ibeacon.pojo.GlobalData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;

public class Tools extends  Activity {

    private static double coefficient1= 0.42093;
    private static double coefficient2= 6.9476;
    private static double coefficient3 =0.54992;
    private static String TAG = "Tools";
    public static  String path="/sdcard/sensorInfo.txt";
   // public static  String path= "file:///android_asset//sensorInfo.txt";
    public static final char[] hexArray = "0123456789ABCDEF".toCharArray();


    public static double CalDistatce(LatLng point1,LatLng point2 ) {
        double lat1=point1.latitude;
        double lat2=point2.latitude;
        double lon1=point1.longitude;
        double lon2=point2.longitude;
        double R = 6371;
        double distance = 0.0;
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
        return distance*1000;
    }

    public static void AppendToConfigFile(Beacon sensor)
    {
        try {

            FileWriter writer = new FileWriter(path,true);
            String msg = sensor.ID+","+sensor.major+","+sensor.minor+","
                    +sensor.position.latitude+","+ sensor.position.longitude
                    +","+sensor.floor+","+sensor.max_rssi+",";
            writer.write(msg+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteToConfigFile(Beacon sensor)
    {
        try {

            FileWriter writer = new FileWriter(path);
            String msg = sensor.ID+","+sensor.major+","+sensor.minor+","
                    +sensor.position.latitude+","+ sensor.position.longitude+","
                    +sensor.floor+","+sensor.max_rssi+",";
            writer.write(msg+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void ReadConfigFile()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String data = br.readLine();
            GlobalData.beaconlist.clear();
            while( data!=null) {
                Beacon sensor = new Beacon();
                String[] info = data.split(",");
               // sensor.mac = info[0];
                sensor.ID = info[0];
                sensor.major= info[1];
                sensor.minor= info[2];
                sensor.position = new LatLng(Double.parseDouble(info[3]),Double.parseDouble(info[4])) ;
                sensor.floor =Integer.parseInt(info[5]);
                sensor.max_rssi = Integer.parseInt(info[6]);

               /* if(info.length > 7){
                    String[] lines = info[7].split("N");
                    Log.e("debug",info[7]+"    "+lines.length);
                    for (int i = 0; i < lines.length; i++){
                        String[] line = lines[i].split("M");
                        String major = line[0];
                        String minor = line[1];
                        sensor.lines.add(new Line(major,minor,0));

                    }
                }*/

                sensor.markerOptions.title(sensor.ID).draggable(true);
                sensor.markerOptions.position(sensor.position);
                sensor.markerOptions.snippet("x:" + sensor.position.latitude + "y:" + sensor.position.latitude + "\n max_rssi:" + sensor.max_rssi);
                GlobalData.beaconlist.put(sensor.ID, sensor);
                Log.e("ReadConfigFile", sensor.toString());
                data = br.readLine();
            }
            br.close();
        }
        catch (IOException e)
        {
                e.printStackTrace();
        }

    }

    public static String formatFloat(double num)
    {
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
        return  httpOperationUtils .doGet(url+param);
    }

    public double calculateDistance(int txPower, double rssi)
    {
        double ratio = rssi*1.0/txPower;
        double distance;

        if (rssi == 0)
        {
            return -1.0;
        }

        if (ratio < 1.0)
        {
            distance =  Math.pow(ratio,10);
        }
        else
        {
            distance =  (coefficient1)*Math.pow(ratio,coefficient2) + coefficient3;
        }
        return distance;
    }

    public static Beacon dealScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5)
        {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
            {
                patternFound = true;
                break;
            }
            startByte++;
        }

        byte[] uuidBytes = new byte[16];
        System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
        String hexString = bytesToHex(uuidBytes);

        String uuid = hexString.substring(0, 8) + "-"
                + hexString.substring(8, 12) + "-"
                + hexString.substring(12, 16) + "-"
                + hexString.substring(16, 20) + "-"
                + hexString.substring(20, 32);


        int major = (scanRecord[startByte + 20] & 0xff) * 0x100
                + (scanRecord[startByte + 21] & 0xff);


        int minor = (scanRecord[startByte + 22] & 0xff) * 0x100
                + (scanRecord[startByte + 23] & 0xff);

        String ibeaconName = device.getName();
        String mac = device.getAddress();
        int txPower = (scanRecord[startByte + 24]);

        Beacon beacon = new Beacon(ibeaconName, uuid,mac,major+"",minor+"",rssi,txPower);

        return  beacon;

    }

    private static String bytesToHex(byte[] bytes)
    {
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
        Beacon max_sensor=null;
        Iterator<String> keyite =  GlobalData.beaconlist.keySet().iterator();
        while (keyite.hasNext())
        {
            String key = keyite.next();
            Beacon sensor = GlobalData.beaconlist.get(key);
            if (sensor.major.equals(major)&&sensor.minor.equals(minor))
            {
                max_sensor = sensor;
            }
        }
        return  max_sensor;
    }


    public static Beacon getMaxRssiSensor(Hashtable<String, Beacon> list)
    {
        Beacon max_sensor=null;
        int max_rssi=-10000;

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

}
