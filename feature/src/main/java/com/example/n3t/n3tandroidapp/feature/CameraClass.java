package com.example.n3t.n3tandroidapp.feature;

import android.content.Context;
import android.content.res.Configuration;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;

import java.io.IOException;

public class CameraClass extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder holder;

    public CameraClass(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters p = camera.getParameters();
        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            p.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            p.setRotation(90);
        } else {
            p.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            p.setRotation(0);
        }

        camera.setParameters(p);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e){}
        camera.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}