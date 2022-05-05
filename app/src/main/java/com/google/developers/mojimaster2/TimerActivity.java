package com.google.developers.mojimaster2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        TimerCustomClass customClass = findViewById(R.id.time);
    }
}