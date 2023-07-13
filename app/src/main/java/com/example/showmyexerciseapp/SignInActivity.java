package com.example.showmyexerciseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    TextView textInputEmail;
    TextView textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        textInputEmail = findViewById(R.id.sgin_in_email);
        textInputPassword = findViewById(R.id.sign_in_password);
    }

    public void SignIn(View v) throws Exception {

        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();
        Log.i("userInput", email);
        Log.i("userInput", password);
        // check input
        if(!Validation.isValidSignInInput(email, password)) {
            Toast toast = Toast.makeText(SignInActivity.this, "Fill all input areas", Toast.LENGTH_SHORT);
            toast.show();
            return;
        };


        // for testing
        String url = "http://10.0.2.2:3000/api/user/sign-in";
        //String url = "https://share-my-exercise-backend.vercel.app/api/user/sign-in";

        // create a json object for request body
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {
            Log.e("LogIn", e.getMessage());
        }

        // make HTTP request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // get json data from response
                        JSONObject data = response.getJSONObject("data");

                        // if there is errors, show error message by toast
                        String status = data.getString("status");
                        if(status.equals("Error")) throw new Exception(data.getString("message"));

                        // save userId for later use
                        JSONObject result = data.getJSONObject("result");
                        int userId = result.getInt("id");
                        saveUserId(userId);

                        Toast toast = Toast.makeText(SignInActivity.this, "Logging in...", Toast.LENGTH_SHORT);
                        toast.show();

                        // navigate to main activity
                        navigateToMainActivity();
                    } catch (Exception e) {
                        Log.e("onResponse", e.getMessage());
                        Toast toast = Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // show error source by toast
                    Log.e("onErrorResponse", error.getMessage());
                    Toast toast = Toast.makeText(SignInActivity.this, "", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        );

        // create request queue
        RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);
        // add http request to the request queue
        // execute the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void navigateToGetStartedActivity(View v) {
        Intent intent = new Intent(SignInActivity.this, GetStartedActivity.class);
        startActivity(intent);
    }

    private void saveUserId(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }

}