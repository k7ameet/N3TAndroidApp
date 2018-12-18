package com.example.n3t.n3tandroidapp.feature;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends AppCompatActivity {

    Button personalProfile, voiceOption;
    static boolean voiceOn = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = new Intent(Settings.this, VoiceService.class);
        personalProfile = (Button)findViewById(R.id.personal_profile);
        voiceOption = (Button)findViewById(R.id.voice_option);

        personalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, PersonalProfile.class));
            }
        });

        voiceOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(voiceOption.getText().equals("Voice Control : Off")){
                    voiceOption.setText("Voice Control : On");
                    voiceOn = true;
                    startService(intent);
                } else if(voiceOption.getText().equals("Voice Control : On")){
                    voiceOption.setText("Voice Control : Off");
                    stopService(intent);
                    voiceOn = false;
                }
            }
        });

    }


}
