package com.example.n3t.n3tandroidapp.feature;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.List;

public class VoiceService extends Service {

    private SpeechRecognizer speechRecognizer;
    public VoiceService() {
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialiseSpeechRecogniser();
        Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent1.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer.startListening(intent1);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(1 == 1){
                    synchronized (this) {
                        try {
                            wait(5000);
                            Log.i("VOICE SERVICE", "5 SECOND LOOP");
                        }catch (java.lang.InterruptedException e){
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
        if(SpeechRecognizer.isRecognitionAvailable(this)){
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

    private void process(String s){
        s = s.toLowerCase();
        if (s.indexOf("photo") != -1){
            if (CameraClass.isCameraOpen){
                if(CameraLayout.camera != null && CameraLayout.cameraAvailable) {
                    CameraLayout.camera.takePicture(null, null, pic);
                    CameraLayout.cameraAvailable = false;
                }
            } else {
                Log.i("PHOTO", "CAMERA CLOSED");
            }
        }
        else if (s.indexOf("location") != -1){
            Log.i("LOCATION", "LOCATION");
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
}
