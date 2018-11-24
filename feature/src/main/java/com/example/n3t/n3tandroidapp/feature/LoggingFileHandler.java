package com.example.n3t.n3tandroidapp.feature;


import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class LoggingFileHandler {

    private String FILE_NAME = "n3t_log.txt";
    private static String url = "https://n3t-api.herokuapp.com/file";


    public void addLogNoImage(String location, Date date){

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "N3T");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append("Location: "+location+", Time: "+date+"\n");
            writer.flush();
            writer.close();
        }catch (Exception e){
            Log.i("ERROR IN LOGGING", e.toString());
        }
    }

    public void addLogYesImage(String location, Date date, String image){

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "N3T");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append("Location: "+location+", Time: "+date+", Image: "+image+"\n");
            writer.flush();
            writer.close();
        }catch (Exception e){
            Log.i("ERROR IN LOGGING", e.toString());
        }
    }

    public static void sendFileToServer () {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    File root = new File(Environment.getExternalStorageDirectory(), "N3T");
                    if (!root.exists()){
                        return;
                    }
                    File file = new File(root, "n3t_log.txt");
                    if (!file.exists()){
                        Log.i("FILE SENDING", "NO FILE");
                        return;
                    }
                    RequestParams params = new RequestParams();
                    params.put("n3t_log", file);
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            Log.i("FILE SENDING", "SUCCESS");
                            deleteLogs();
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("FILE SENDING", "FAILURE");
                        }

                    });
                }catch (Exception e){
                    Log.i("ERROR IN SENDING TO SERVER", e.toString());
                }
            }
        };
        mainHandler.post(myRunnable);

    }

    private static void deleteLogs () {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "N3T");
            if (!root.exists()) {
                return;
            }
            File file = new File(root, "n3t_log.txt");
            if (file.exists()) {
                file.delete();
                Log.i("FILE DELETE", "SUCCESS");
            }
        }catch(Exception e){
            Log.i("FILE DELETE", "ERROR");
        }
    }

}
