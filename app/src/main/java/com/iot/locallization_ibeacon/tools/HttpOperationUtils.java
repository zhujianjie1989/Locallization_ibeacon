package com.iot.locallization_ibeacon.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpOperationUtils
{
	public String doGet(String url)
    {
		String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet())
            {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
           Log.e("dddddd", "发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }

	    return result;
	}

    private List<GeoPoint> decodePoly(String encoded)
    {

        List<GeoPoint> poly = new ArrayList<GeoPoint>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len)
        {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((int) ((lat / 1E5) * 1E6),(int) ((lng / 1E5) * 1E6));
            poly.add(p);
        }

        return poly;
    }

    public List<LatLng> getPoint(String strResult)
    {

        List<LatLng> points = new ArrayList<LatLng>();
        if (-1 == strResult.indexOf("<status>OK</status>"))
        {
            return null;
        }

        int pos = strResult.indexOf("<overview_polyline>");
        pos = strResult.indexOf("<points>", pos + 1);
        int pos2 = strResult.indexOf("</points>", pos);
        strResult = strResult.substring(pos + 8, pos2);

        List<GeoPoint> geoPoints = decodePoly(strResult);

        LatLng ll;
        for (Iterator<GeoPoint> gpit = geoPoints.iterator(); gpit.hasNext();) {
            GeoPoint gp = gpit.next();
            double latitude = gp.lat;
            latitude = latitude / 1000000;

            double longitude = gp.lng;
            longitude = longitude / 1000000;

            System.out.println("{" + latitude + "," + longitude + "}");
            ll = new LatLng(latitude, longitude);
            points.add(ll);
        }

        return points;
    }

    public List<LatLng> getJson(String strResult) {

        List<LatLng> points = new ArrayList<LatLng>();
        if (-1 == strResult.indexOf("<status>OK</status>"))
        {
            return null;
        }

        int pos = strResult.indexOf("<overview_polyline>");
        pos = strResult.indexOf("<points>", pos + 1);
        int pos2 = strResult.indexOf("</points>", pos);
        strResult = strResult.substring(pos + 8, pos2);

        List<GeoPoint> geoPoints = decodePoly(strResult);

        LatLng ll;

        for (Iterator<GeoPoint> gpit = geoPoints.iterator(); gpit.hasNext();)
        {
            GeoPoint gp = gpit.next();
            double latitude = gp.lat;
            latitude = latitude / 1000000;

            double longitude = gp.lng;
            longitude = longitude / 1000000;

            ll = new LatLng(latitude, longitude);
            points.add(ll);
        }

        return points;
    }


    class LatLng
    {
        double lat;
        double lng;

        public LatLng(double latt, double lngg) {
            lat = latt;
            lng = lngg;
        }

    }

    class GeoPoint
    {
        int lat;
        int lng;

        public GeoPoint(int latt, int lngg)
        {
            lat = latt;
            lng = lngg;
        }

    }
	
   
}

