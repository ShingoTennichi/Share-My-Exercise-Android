package com.example.showmyexerciseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import Components.Navigate;

public class GetStartedActivity extends AppCompatActivity {

    Button getStartedBtn;
    Button signInBtn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        // check if user is already logged in
        sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        int isLoggedIn = sharedPreferences.getInt("userId", -1);
        // if not -1, user already logged in
        if(isLoggedIn != -1) {
            Navigate.navigateToNextActivity(GetStartedActivity.this, MainActivity.class);
            finish();
        }

        getStartedBtn = findViewById(R.id.btn_getStarted);
        signInBtn = findViewById(R.id.btn_nav_signIn);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigate.navigateToNextActivity(GetStartedActivity.this, SignUpActivity.class);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigate.navigateToNextActivity(GetStartedActivity.this, SignInActivity.class);
            }
        });
    }
}