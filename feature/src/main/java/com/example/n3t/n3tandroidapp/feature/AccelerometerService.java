package com.example.n3t.n3tandroidapp.feature;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import java.util.Calendar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

//Updates the server with the phones current accelerometer and location data every 'x' seconds.
//x is currently 5 but can be changed in the OnStartCommand method.
//Sends data as a JSON object using HTTP POST request.

public class AccelerometerService extends Service implements SensorEventListener{

    private Sensor sensor;
    private SensorManager sm;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private String urlString = "https://n3t-portal.herokuapp.com/postDataLocation";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {}

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) {}
        };

        try {
            locationManager.requestLocationUpdates("gps", 100, 0, locationListener);
        } catch (SecurityException e) {}

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
        locationManager.removeUpdates(locationListener);
        sm.unregisterListener(this);
    }


    private void sendUpdates(JSONObject jsonObject) {
        Log.i("accelerometer", x + " " + y + " " + z);

        RequestQueue q = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("http", "got response");
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("http", "no response");
            }
        };

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlString, jsonObject, success, failure);
        q.add(req);

    }

    //Create JSON object to send.
    private JSONObject makeJsonObject(double x, double y, double z) {
        JSONObject o = new JSONObject();
        try {
            Date currentTime = Calendar.getInstance().getTime();
            try {
                location = locationManager.getLastKnownLocation("gps");
            } catch (SecurityException e){}
            o.put("IMU_x", x);
            o.put("IMU_y", y);
            o.put("IMU_z", z);
            o.put("dateTime", currentTime); //2018-09-06T23:43:51.000Z
            o.put("humidity", "-100");
            o.put("barometricPressure", "-100");
            if (location == null){
                o.put("longitude", "-100");
                o.put("latitude", "-100");
            } else {
                o.put("longitude", location.getLongitude());
                o.put("latitude", location.getLatitude());
            }
            o.put("temperature", "-100");
            o.put("windSpeed", "-100");
            o.put("photo", "");
            o.put("id", 0);
            Log.i("JSON", o.toString());

        } catch (JSONException e) {
            Log.i("json", e.toString());
        }
        return o;
    }


    //Update accelerometer readings whenever the phone moves.
    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
