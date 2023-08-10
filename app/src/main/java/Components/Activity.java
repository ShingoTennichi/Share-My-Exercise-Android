package Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.showmyexerciseapp.BuildConfig;
import com.example.showmyexerciseapp.WorkoutResultFragment;

import org.json.JSONObject;

import java.time.LocalDateTime;

public class Activity implements SaveActivityInterface {

    private int id;
    private String exerciseName;
    private LocalDateTime startedAt;
    private LocalDateTime restartedAt;
    private LocalDateTime finishedAt;
    private int duration;

    public int getId() {
        return id;
    }

    public String getExerciseName() { return exerciseName; }

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

    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

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

    public void saveActivity(Context context) {

        // make sure Build Variant is debug or release
        String requestPath = "/api/activity/save-activity";
        String url = BuildConfig.BASE_URL + requestPath;

        // create a json object for request body
        JSONObject json = new JSONObject();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("config_file", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            json.put("authorId", userId);
            json.put("activityId", this.id);
            json.put("startedAt", this.startedAt);
            json.put("finishedAt", this.finishedAt);
            json.put("duration", this.duration);
        } catch (Exception e) {
            Log.e("saveActivityData", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
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

                        JSONObject result = data.getJSONObject("result");

                        // bundle activity data to pass next activity
                        Bundle bundle = new Bundle();
                        bundle.putInt("time", result.getInt("duration"));
                        WorkoutResultFragment workoutResultFragment = new WorkoutResultFragment();
                        workoutResultFragment.setArguments(bundle);

                        // navigate to Workout Result Fragment
                        Navigate.navigateToNextFragment(context, workoutResultFragment);

                        Toast.makeText(context, "Activity data saved", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("onResponse", "" + e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse", "" + error.getMessage());
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );

        // create request queue and add http request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // execute the queue
        requestQueue.add(jsonObjectRequest);
    }
}
