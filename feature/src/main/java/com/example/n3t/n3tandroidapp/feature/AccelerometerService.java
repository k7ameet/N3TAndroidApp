package com.example.n3t.n3tandroidapp.feature;


import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class AccelerometerService extends Service implements SensorEventListener{

    private Sensor sensor;
    private SensorManager sm;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private String url = "https://n3t-portal.herokuapp.com/postDataLocation";
    private HashMap<String, String> hm;
    private String jsonTest = "{\"IMU_x\":\"123\",\"IMU_y\":\"12\"\"IMU_z\":\"1\",\"dateTime\":\"9/6\",\"barometricPressure\":\"\",\"longitude\":\"50\",\"latitude\":\"51\",\"temperature\":\"\",\"windSpeed\":\"\",\"photo\":\"\",\"id\":\"123\"}";

    public AccelerometerService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.i("onStartCommand", "Service started");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(1 == 1){
                    synchronized (this) {
                        try {
                            wait(5000);
                            Log.i("onStartCommand", "Service running");

                            hm = new HashMap<String, String>();
                            hm.put("IMU_x", "123");
                            hm.put("IMU_y", "12");
                            hm.put("IMU_z", "1");
                            hm.put("dateTime", "9/6");
                            hm.put("barometricPressure", "");
                            hm.put("longitude", "50");
                            hm.put("latitude", "51");
                            hm.put("temperature", "");
                            hm.put("windSpeed", "");
                            hm.put("photo", "");
                            hm.put("id", "123");


                            sendUpdates(url, hm);
                        }catch (java.lang.InterruptedException e){}
                    }
                }
            }
        };

        Thread t = new Thread(r);
        t.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy", "Service destroyed");
    }


    private String sendUpdates(String requestURL, HashMap<String, String> postDataParams) {
        Log.i("accelerometer", x+" "+y+" "+z);


            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
                result.append("{");
            }

            result.append("\"" + entry.getKey() + "\"" + ":" + "\"" + entry.getValue() + "\",");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String res = removeLastChar(result.toString());
        res = res + "}";

        //return res;
        return jsonTest;
    }

    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
