package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class DisplayImage extends Activity  {

    private String urlString = "https://n3t-api.herokuapp.com/postDataLocationPhotoStringJSON";
    private Handler mWaitHandler = new Handler();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    private Bitmap rotatedImage;
    private String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);



        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    ((TextView) findViewById(R.id.coordinates)).setText("Turn GPS on for location");
                }
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 1);
                return;
            }
            locationManager.requestLocationUpdates("gps", 100, 0, locationListener);

            if (Camera.imageTempStore == null) {
                ((TextView) findViewById(R.id.coordinates)).setText("Error retrieving image");
            } else {

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                byte[] bytes = Camera.bytesTempStore;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                rotatedImage = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), matrix, true);
                ((ImageView) findViewById(R.id.current_image)).setImageBitmap(rotatedImage);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedImage.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            location = locationManager.getLastKnownLocation("gps");
            Date time = Calendar.getInstance().getTime();
            // NULL POINTER EXCEPTION
            if (location != null) {
                ((TextView) findViewById(R.id.coordinates)).setText("Location: (" + Math.round(location.getLatitude() * 100) / 100 + ", " + Math.round(location.getLongitude() * 100) / 100 + "), Date: " + time);
            }

            sendUpdates(makeJsonObject());
        }catch(OutOfMemoryError oom){
            Context context = getApplicationContext();
            CharSequence text = "Error: this photo was not sent to server";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            startActivity(new Intent(DisplayImage.this, Camera.class));
            Log.i("ERROR OUT OF MEMORY", oom.getMessage());
            finish();
        }

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
        locationManager.removeUpdates(locationListener);
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
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

        RequestQueue q = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("got response photo", response.toString());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("NOOOOOOOOOOOOOOOOOOO", error.toString());
            }
        };

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlString, jsonObject, success, failure);
        req.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        q.add(req);




    }

    private JSONObject makeJsonObject() {


        JSONObject o = new JSONObject();
        try {
            Date currentTime = Calendar.getInstance().getTime();
            try {
                location = locationManager.getLastKnownLocation("gps");
            } catch (SecurityException e){}
            o.put("IMU_x", "-200");
            o.put("IMU_y", "-200");
            o.put("IMU_z", "-200");
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
            o.put("photo", "data:image/png;base64,"+encodedImage);
            Log.i("ENCODED IMAGE", encodedImage);
            o.put("id", 0);

        } catch (JSONException e) {
            Log.i("json", "json fail");
        }
        return o;
    }

}
