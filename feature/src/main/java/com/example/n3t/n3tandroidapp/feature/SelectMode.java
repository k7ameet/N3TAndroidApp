package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.intentfilter.androidpermissions.PermissionManager;

import static java.util.Collections.singleton;

public class SelectMode extends AppCompatActivity {

    Button car, rmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        startService(serviceIntent);




        car = (Button)findViewById(R.id.car_button);
        rmc = (Button)findViewById(R.id.rmc_button);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectMode.this, Camera.class));
                finish();
            }
        });


        rmc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectMode.this, Camera.class));
                finish();
            }
        });



    }
}
