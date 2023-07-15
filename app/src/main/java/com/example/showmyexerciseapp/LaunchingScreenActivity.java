package com.example.showmyexerciseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import Components.Navigate;

public class LaunchingScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Navigate.navigateToNextActivity(LaunchingScreenActivity.this, GetStartedActivity.class);
                finish();
            }
        },3000);
    }
}