package com.example.n3t.n3tandroidapp.feature;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AccelerometerService extends Service implements SensorEventListener{

    private Sensor sensor;
    private SensorManager sm;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private String urlString = "https://n3t-portal.herokuapp.com/postDataLocation";
    private String urlTest = "";
    private String jsonTest = "{\n\"IMU_x\":\"123\",\n\"IMU_y\":\"12\"\"IMU_z\":\"1\",\n\"dateTime\":\"9/6\",\n\"barometricPressure\":\"\",\n\"longitude\":\"50\",\n\"latitude\":\"51\",\n\"temperature\":\"\",\n\"windSpeed\":\"\",\n\"photo\":\"\",\n\"id\":\"123\"\n}";

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
                            sendUpdates(makeJsonObject(x, y, z));
                        }catch (java.lang.InterruptedException e){
                            Log.i("run", e.toString());
                        }
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


    private void sendUpdates(JSONObject jsonObject) {
        Log.i("accelerometer", x + " " + y + " " + z);

        try {
            URL url = new URL(urlString);
            //URL url = new URL(urlTest);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept","application/json");
            httpURLConnection.connect();

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            //Log.i("JSON", jsonObject.toString());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();
        } catch(Exception e) {
            Log.i("Http post", e.toString());
        }
    }

    private JSONObject makeJsonObject(double x, double y, double z) {
        JSONObject o = new JSONObject();
        try {
            o.put("IMU_x", x);
            o.put("IMU_y", y);
            o.put("IMU_z", z);
            o.put("dateTime", "");
            o.put("barometricPressure", "");
            o.put("longitude", "");
            o.put("latitude", "");
            o.put("temperature", "");
            o.put("windspeed", "");
            o.put("photo", "");
            o.put("id", 5);

        } catch (JSONException e) {
            Log.i("json", e.toString());
        }
        return o;
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
