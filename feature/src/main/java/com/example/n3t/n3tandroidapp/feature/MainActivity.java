package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button car, truck, rmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        startService(serviceIntent);

        car = (Button)findViewById(R.id.car_button);
        truck = (Button)findViewById(R.id.truck_button);
        rmc = (Button)findViewById(R.id.rmc_button);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Camera.class));
            }
        });

        truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Camera.class));
            }
        });

        rmc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Camera.class));
            }
        });


    }
}
