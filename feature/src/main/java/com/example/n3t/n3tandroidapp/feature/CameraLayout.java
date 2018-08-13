package com.example.n3t.n3tandroidapp.feature;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.hardware.Camera;

public class CameraLayout extends AppCompatActivity {

    Camera camera;
    FrameLayout fl;
    CameraClass cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        fl = (FrameLayout)findViewById(R.id.camera_frame);

        camera = Camera.open();

        cameraPreview = new CameraClass(this, camera);
        fl.addView(cameraPreview);
    }
}
