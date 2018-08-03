package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Intent;
import android.graphics.Path;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

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

        exit = (Button)findViewById(R.id.exit_btn);
        start = (Button)findViewById(R.id.start_taking_photos_btn);
        options = (Button)findViewById(R.id.options_btn);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectMode.class));
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectMode.class));
                finish();
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Options.class));
                finish();
            }
        });
    }
}
