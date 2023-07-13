package com.example.showmyexerciseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDateTime;

public class Activity {

    private int id;
    private String name;
    private LocalDateTime startedAt;
    private LocalDateTime restartedAt;
    private LocalDateTime finishedAt;
    private int duration;

    public Activity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getRestartedAt() {
        return restartedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public int getDuration() {
        return duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setRestartedAt(LocalDateTime restartedAt) {
        this.restartedAt = restartedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void initActivityTime(LocalDateTime startedAt) {

        this.startedAt = startedAt;
        this.restartedAt = this.startedAt;
        duration = 0;
    }

    public void saveActivityData(Context context) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("config_file", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        JSONObject json = new JSONObject();
        try {

            json.put("authorId", userId);
            // #########
            json.put("activityId", this.id);
            // #########
            json.put("startedAt", this.startedAt);
            json.put("finishedAt", this.finishedAt);
            json.put("duration", this.duration);
        } catch (Exception e) {
            Log.e("saveActivityData", e.getMessage());
            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

        // for testing
        String url = "http://10.0.2.2:3000/api/activity/save-activity";
        //String url = "";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                    } catch (Exception e) {
                        Log.e("onResponse", "" + e.getMessage());
                        Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse", "" + error.getMessage());
                    Toast toast = Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        );

        // create request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // add http request to the request queue
        // execute the queue
        requestQueue.add(jsonObjectRequest);
    }
}
