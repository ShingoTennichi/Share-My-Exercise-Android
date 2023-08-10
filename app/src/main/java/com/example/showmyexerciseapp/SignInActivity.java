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

public class SignInActivity extends AppCompatActivity {

    TextView textInputEmail;
    TextView textInputPassword;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        textInputEmail = findViewById(R.id.sgin_in_email);
        textInputPassword = findViewById(R.id.sign_in_password);
        backBtn = findViewById(R.id.btn_back_SI);

        backBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigate.navigateToNextActivity(SignInActivity.this, GetStartedActivity.class);
                    finish();
                }
            }
        );
    }

    public void signIn(View v) {
        Toast.makeText(SignInActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();

        if(!Validation.isValidSignInInput(email, password)) {
            Toast.makeText(SignInActivity.this, "Fill all input areas", Toast.LENGTH_SHORT).show();
            return;
        };

        // make sure Build Variant is debug or release
        String requestPath = "/api/user/sign-in";
        String url = BuildConfig.BASE_URL + requestPath;

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
                        JSONObject data = response.getJSONObject("data");

                        String status = data.getString("status");
                        if(status.equals("error")) throw new Exception("Make sure email and password are correct");

                        // save userId for later use
                        JSONObject result = data.getJSONObject("result");
                        int userId = result.getInt("id");
                        saveUserId(userId);

                        Navigate.navigateToNextActivity(SignInActivity.this, MainActivity.class);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SignInActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void saveUserId(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("config_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }

}