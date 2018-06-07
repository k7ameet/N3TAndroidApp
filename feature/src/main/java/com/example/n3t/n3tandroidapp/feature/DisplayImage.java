package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class DisplayImage extends Activity {
    private Handler mWaitHandler = new Handler();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Image image = Camera.imageTempStore;

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
            Bitmap rotatedImage = Bitmap.createBitmap(bitmapImage ,0 ,0 ,bitmapImage.getWidth() ,bitmapImage.getHeight() ,matrix ,true);
            ((ImageView)findViewById(R.id.current_image)).setImageBitmap(rotatedImage);
        }

        location = locationManager.getLastKnownLocation("gps");
        Date time = Calendar.getInstance().getTime();
        // NULL POINTER EXCEPTION
        if(location != null) {
            ((TextView) findViewById(R.id.coordinates)).setText("Location: (" + Math.round(location.getLatitude() * 100) / 100 + ", " + Math.round(location.getLongitude() * 100) / 100 + "), Date: " + time);
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
}
