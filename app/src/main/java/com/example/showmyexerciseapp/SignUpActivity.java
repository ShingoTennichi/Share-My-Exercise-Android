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



public class SignUpActivity extends AppCompatActivity {

    TextView textInputFName;
    TextView textInputLName;
    TextView textInputEmail;
    TextView textInputPassword;
    TextView textInputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputFName = findViewById(R.id.fName);
        textInputLName = findViewById(R.id.lName);
        textInputEmail = findViewById(R.id.email);
        textInputPassword = findViewById(R.id.password);
        textInputConfirmPassword = findViewById(R.id.confirm_password);
    }
    private void navigateToMainActivity() {
        // navigate to main activity after successfully create user account
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void navigateToGetStarted(View v) {
        // for back button
        Intent intent = new Intent(SignUpActivity.this, GetStartedActivity.class);
        startActivity(intent);
    }

    public void createAccount(View v) throws Exception {
        // get user input
        String fName = textInputFName.getText().toString();
        String lName = textInputLName.getText().toString();
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();
        String confirmPassword = textInputConfirmPassword.getText().toString();

        //
        try {
            if(Validation.signUpValidation(fName, lName, email, password, confirmPassword))  {
                // connect to db to create account
                postUserData(fName,lName,email,password);
            } else {
                throw new Exception("invalid input");
            }
        } catch(Exception e) {
            Log.e("createAccount", e.getMessage());
            Toast toast = Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void postUserData(String fName, String lName, String email, String password) throws Exception {

        // for testing
        String url = "http://10.0.2.2:3000/api/user/sign-up";
        //String url = "https://share-my-exercise-backend.vercel.app/api/user/sign-up";

        // create a json object for request body
        JSONObject json = new JSONObject();
        try {
            json.put("firstName", fName);
            json.put("lastName", lName);
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {
            Log.e("postUserData: ", e.getMessage());
            throw new Exception(e.getMessage());
        }

        // make HTTP request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // get json data from response
                        JSONObject data  = response.getJSONObject("data");

                        // if there is errors, show error message by toast
                        String status = data.getString("status");
                        if(status.equals("Error")) throw new Exception(data.getString("message"));

                        // save userId for later use
                        JSONObject userData = data.getJSONObject("result");
                        int userId = userData.getInt("id");
                        saveUserId(userId);

                        // navigate to main activity
                        navigateToMainActivity();
                    } catch (Exception e) {
                        Log.e("onResponse: ", e.getMessage());
                        Toast toast = Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // show error source by toast
                    Log.e("onErrorResponse", error.getMessage());
                    Toast toast = Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        );

        // create request queue
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        // add http request to the request queue
        // execute the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void saveUserId(int userId) {
        // save user data to the preference file
        SharedPreferences sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }
}