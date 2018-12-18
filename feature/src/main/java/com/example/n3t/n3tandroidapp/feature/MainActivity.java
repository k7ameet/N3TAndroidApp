package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.intentfilter.androidpermissions.PermissionManager;

import static java.util.Collections.singleton;

public class MainActivity extends AppCompatActivity {


    Button exit, start, options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        // Start recording IMU data
        // This service is still under development, and is therefore not being used

        /*Intent serviceIntent = new Intent(this, AccelerometerService.class);
        startService(serviceIntent);*/

        // Service for voice recording
        // In a later update, launching of this service was moved to "Settings" activity
        // Option still remains to start from here

        /*Intent serviceIntent1 = new Intent(this, VoiceService.class);
        startService(serviceIntent1);*/

        //Set global action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        //Get all necessary permission as soon as app is opened for the first time
        PermissionManager permissionManager = PermissionManager.getInstance(this);
        permissionManager.checkPermissions(singleton(Manifest.permission.ACCESS_FINE_LOCATION), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.ACCESS_COARSE_LOCATION), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.INTERNET), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.RECORD_AUDIO), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.CAMERA), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.READ_PHONE_STATE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
        permissionManager.checkPermissions(singleton(Manifest.permission.ACCESS_NETWORK_STATE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });

        exit = (Button) findViewById(R.id.exit_btn);
        start = (Button) findViewById(R.id.start_taking_photos_btn);
        options = (Button) findViewById(R.id.options_btn);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectMode.class));
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Options.class));
            }
        });

        String s = Build.SERIAL;

        try {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            }
            String imei = telephonyManager.getImei();
            if (imei != null) {
                DetailsStore.setIMEI(imei);
            }
        } catch (Exception e){
            Log.i("Error getting IMEI", e.getMessage());
        }
    }

}
