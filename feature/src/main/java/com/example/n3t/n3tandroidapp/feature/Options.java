package com.example.n3t.n3tandroidapp.feature;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Options extends AppCompatActivity {

    Button settings, about, credits, support;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        settings = (Button)findViewById(R.id.settings_btn);
        about = (Button)findViewById(R.id.about_btn);
        credits = (Button)findViewById(R.id.credits_btn);
        support = (Button)findViewById(R.id.support_btn);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Options.this, About.class));
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Options.this, Credits.class));
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Options.this, Support.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Options.this, Settings.class));
            }
        });



    }

}
