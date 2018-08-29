package com.example.n3t.n3tandroidapp.feature;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.hardware.Camera;
import android.widget.ImageButton;

//Using deprecated camera class as it contains features required for this app.
public class CameraLayout extends AppCompatActivity {

    Camera camera;
    FrameLayout fl;
    CameraClass cameraPreview;
    ImageButton click;
    static byte[] imageAsByteArray;
    boolean cameraAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        fl = (FrameLayout)findViewById(R.id.camera_frame);


        camera = Camera.open();

        cameraPreview = new CameraClass(this, camera);
        fl.addView(cameraPreview);

        cameraAvailable = true;

        click = (ImageButton)findViewById(R.id.camera_click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickButton(view);
            }
        });
    }

    Camera.PictureCallback pic = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            imageAsByteArray = data;
            startActivity(new Intent(CameraLayout.this, DisplayImage.class));
            finish();
        }
    };

    public void clickButton(View v) {
        if(camera != null && cameraAvailable) {
            camera.takePicture(null, null, pic);
            cameraAvailable = false;
        }

    }
}
