package com.example.showmyexerciseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GetStartedActivity extends AppCompatActivity {

    Button getStartedBtn;
    Button signInBtn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        getStartedBtn = findViewById(R.id.btn_getStarted);
        signInBtn = findViewById(R.id.btn_nav_signIn);

        // check if user is already logged in
        try {
            sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
            int isLoggedIn = sharedPreferences.getInt("userId", -1);
            Log.i("sharedPreferences", "login status: " + isLoggedIn);
            if(isLoggedIn != -1) navigateToMainActivity();
        } catch (Exception e) {
            // show errors by toast
            Log.i("sharedPreferences", e.getMessage());
            Toast toast = Toast.makeText(GetStartedActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(GetStartedActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void navigateToSignUp(View v) {
        Intent intent = new Intent(GetStartedActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void navigateToSignIn(View v) {
        Intent intent = new Intent(GetStartedActivity.this, SignInActivity.class);
        startActivity(intent);
    }
}