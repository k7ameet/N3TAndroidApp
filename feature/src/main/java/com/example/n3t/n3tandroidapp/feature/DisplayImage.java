package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DisplayImage extends Activity implements SensorEventListener {

    private Sensor sensor;
    private SensorManager sm;
    private String urlString = "https://n3t-portal.herokuapp.com/postDataLocationFile";
    private Handler mWaitHandler = new Handler();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    private Bitmap rotatedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Image image = Camera.imageTempStore;

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
            public void onProviderDisabled(String provider) {
                ((TextView)findViewById(R.id.coordinates)).setText("Turn GPS on for location");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 1);
            return;
        }
        locationManager.requestLocationUpdates("gps", 100, 0, locationListener);

        if(image == null){
            ((TextView)findViewById(R.id.coordinates)).setText("Error retrieving image");
        }
        else{

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            byte[] bytes = Camera.bytesTempStore;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            rotatedImage = Bitmap.createBitmap(bitmapImage ,0 ,0 ,bitmapImage.getWidth() ,bitmapImage.getHeight() ,matrix ,true);
            ((ImageView)findViewById(R.id.current_image)).setImageBitmap(rotatedImage);
        }

        location = locationManager.getLastKnownLocation("gps");
        Date time = Calendar.getInstance().getTime();
        // NULL POINTER EXCEPTION
        if(location != null) {
            ((TextView) findViewById(R.id.coordinates)).setText("Location: (" + Math.round(location.getLatitude() * 100) / 100 + ", " + Math.round(location.getLongitude() * 100) / 100 + "), Date: " + time);
        }

        sendUpdates(makeJsonObject());

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //The following code will execute after the 3 seconds.

                try {

                    //Go to next page i.e, start the next activity.
                    Intent intent = new Intent(getApplicationContext(), Camera.class);
                    startActivity(intent);

                    //Let's Finish Splash Activity since we don't want to show this when user press back button.
                    finish();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 3000);  // Give a 3 seconds delay.
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), DisplayImage.class);
                startActivity(intent);
            }
        }
    }

    private void sendUpdates(JSONObject jsonObject) {
        Log.i("send updates", Camera.x1 + " " + Camera.y1 + " " + Camera.z1);

        /*RequestQueue q = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("got response photo", response.toString());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("no photo response", error.toString());
            }
        };

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlString, jsonObject, success, failure);
        q.add(req);*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(DisplayImage.this,response,Toast.LENGTH_LONG).show();
                        Log.i("PHOTO SUCCESS", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(DisplayImage.this,error.toString(),Toast.LENGTH_LONG).show();
                        Log.i("PHOTO FAILURE", error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Date currentTime = Calendar.getInstance().getTime();
                try {
                    location = locationManager.getLastKnownLocation("gps");
                } catch (SecurityException e){}
                Map<String,String> params = new HashMap<String, String>();
                params.put("IMU_x",String.valueOf(Camera.x1));
                params.put("IMU_y", String.valueOf(Camera.y1));
                params.put("IMU_z", String.valueOf(Camera.z1));
                params.put("dateTime", currentTime.toString());
                params.put("humidity", "-200");
                params.put("barometricPressure", "-200");
                params.put("longitude", String.valueOf(location.getLongitude()));
                params.put("latitude", String.valueOf(location.getLatitude()));
                params.put("temperature", "-200");
                params.put("windSpeed", "-200");
                params.put("photo", "");
                params.put("id", "0");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private JSONObject makeJsonObject() {

        /*File f = null;

        try {
            //create a file to write bitmap data
            f = new File(this.getFilesDir(), "image.png");
            f.createNewFile();

            Bitmap bitmap = rotatedImage;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 0, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            Log.i("FILE CREATION", "SUCCESS");
        } catch(IOException e){
            Log.i("FILE CREATION ERROR", e.getMessage());
        }*/


        JSONObject o = new JSONObject();
        try {
            Date currentTime = Calendar.getInstance().getTime();
            try {
                location = locationManager.getLastKnownLocation("gps");
            } catch (SecurityException e){}
            o.put("IMU_x", Camera.x1);
            o.put("IMU_y", Camera.y1);
            o.put("IMU_z", Camera.z1);
            o.put("dateTime", currentTime);
            o.put("humidity", "-200");
            o.put("barometricPressure", "-200");
            if (location == null){
                o.put("longitude", "-200");
                o.put("latitude", "-200");
            } else {
                o.put("longitude", location.getLongitude());
                o.put("latitude", location.getLatitude());
            }
            o.put("temperature", "-200");
            o.put("windSpeed", "-200");
            o.put("photo", Camera.TheFileToSend);
            o.put("id", 0);
            Log.i("JSON", o.toString());

        } catch (JSONException e) {
            Log.i("json", "json fail");
        }
        return o;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
