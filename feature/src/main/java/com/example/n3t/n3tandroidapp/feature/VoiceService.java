package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;

public class VoiceService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat = -50000;
    private double lon = -50000;
    private Date currentDT;
    private String locationString;

    private SpeechRecognizer speechRecognizer;

    public VoiceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                locationString = "GPS OFF";
            }
        };

        try {
            locationManager.requestLocationUpdates("gps", 100, 0, locationListener);
        } catch (SecurityException e) {
        }

        initialiseSpeechRecogniser();
        Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent1.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer.startListening(intent1);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (1 == 1) {
                    synchronized (this) {
                        try {
                            wait(5000);
                            Log.i("VOICE SERVICE", "5 SECOND LOOP");
                        } catch (java.lang.InterruptedException e) {
                            Log.i("VOICE SERVICE", "LOOP ERROR");
                        }
                    }
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        return Service.START_STICKY;

    }

    private void initialiseSpeechRecogniser() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {
                    Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent1.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    speechRecognizer.startListening(intent1);

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    process(result.get(0));

                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent1.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    speechRecognizer.startListening(intent1);

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void process(String s) {
        s = s.toLowerCase();
        if (s.indexOf("photo") != -1) {
            if (CameraClass.isCameraOpen) {
                if (CameraLayout.camera != null && CameraLayout.cameraAvailable) {
                    CameraLayout.camera.takePicture(null, null, pic);
                    CameraLayout.cameraAvailable = false;
                }
            } else {
                Log.i("PHOTO", "CAMERA CLOSED");
            }
        } else if (s.indexOf("location") != -1) {
            Log.i("LOCATION", "LOCATION");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if(lat!=-50000&&lon!=-50000){
                locationString = convertCoordinatesToDegrees(lat, lon);
            }
            currentDT = new Date();
            LoggingFileHandler logger = new LoggingFileHandler();
            logger.addLog(locationString, currentDT);
        }
        else {
            Log.i("SOMETHING ELSE", "SOMETHING ELSE");
        }

        Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent1.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer.startListening(intent1);

    }

    Camera.PictureCallback pic = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            CameraLayout.imageAsByteArray = data;
            Intent intent = new Intent(VoiceService.this, DisplayImage.class);
            startActivity(intent);

        }
    };

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
    }

    //Helper method that converts coordinates from decimal to direction/degree/minute/second.
    //This is easier for the user to understand and looks better.
    //Could make a new class with static method to replace this method if this activity starts lagging.
    private String convertCoordinatesToDegrees (double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("Lat: S ");
        } else {
            builder.append("Lat: N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append("\n");

        if (longitude < 0) {
            builder.append("Lon: W ");
        } else {
            builder.append("Lon: E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

}
