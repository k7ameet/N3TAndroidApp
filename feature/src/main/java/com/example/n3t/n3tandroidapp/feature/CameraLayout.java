package com.example.n3t.n3tandroidapp.feature;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.hardware.Camera;
import android.widget.ImageButton;

public class CameraLayout extends AppCompatActivity {

    Camera camera;
    FrameLayout fl;
    CameraClass cameraPreview;
    ImageButton click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        fl = (FrameLayout)findViewById(R.id.camera_frame);

        camera = Camera.open();

        cameraPreview = new CameraClass(this, camera);
        fl.addView(cameraPreview);

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
            Intent intent = new Intent(CameraLayout.this, DisplayImage.class);
            Bundle b = new Bundle();
            b.putByteArray("byteArray", data);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
    };

    public void clickButton(View v) {
        if(camera != null) {
            camera.takePicture(null, null, pic);
        }

    }
}
