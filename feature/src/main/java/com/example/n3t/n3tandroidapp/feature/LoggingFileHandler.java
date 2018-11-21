package com.example.n3t.n3tandroidapp.feature;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class LoggingFileHandler {

    private String FILE_NAME = "n3t_log.txt";

    public void addLog(String location, Date date){

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "N3T");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append("Location: "+location+", Time: "+date);
            writer.flush();
            writer.close();
        }catch (Exception e){
            Log.i("ERROR IN LOGGING", e.toString());
        }
    }

}
