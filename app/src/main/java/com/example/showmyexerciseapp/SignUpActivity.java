package com.example.showmyexerciseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import Components.Navigate;
import Components.Validation;

public class SignUpActivity extends AppCompatActivity {

    TextView textInputFName;
    TextView textInputLName;
    TextView textInputEmail;
    TextView textInputPassword;
    TextView textInputConfirmPassword;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputFName = findViewById(R.id.fName);
        textInputLName = findViewById(R.id.lName);
        textInputEmail = findViewById(R.id.email);
        textInputPassword = findViewById(R.id.password);
        textInputConfirmPassword = findViewById(R.id.confirm_password);
        backBtn = findViewById(R.id.btn_back_SU);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigate.navigateToNextActivity(SignUpActivity.this, GetStartedActivity.class);
                finish();
            }
        });
    }

    public void signUp(View v) {
        String fName = textInputFName.getText().toString();
        String lName = textInputLName.getText().toString();
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();
        String confirmPassword = textInputConfirmPassword.getText().toString();

        try {
            if(Validation.signUpValidation(fName, lName, email, password, confirmPassword))  {
                postUserData(fName,lName,email,password);
            } else {
                throw new Exception("Fill all areas and make sure password");
            }
        } catch(Exception e) {
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void postUserData(String fName, String lName, String email, String password) throws Exception {

        // make sure Build Variant is debug or release
        String requestPath = "/api/user/sign-up";
        String url = BuildConfig.BASE_URL + requestPath;

        JSONObject json = new JSONObject();
        try {
            json.put("firstName", fName);
            json.put("lastName", lName);
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data  = response.getJSONObject("data");
                        String status = data.getString("status");
                        if(status.equals("error")) throw new Exception("The Email is already taken");

                        // save userId for later use
                        JSONObject userData = data.getJSONObject("result");
                        int userId = userData.getInt("id");
                        saveUserId(userId);

                        Navigate.navigateToNextActivity(SignUpActivity.this, MainActivity.class);
                        Toast.makeText(SignUpActivity.this, "Successfully signed up", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void saveUserId(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }
}