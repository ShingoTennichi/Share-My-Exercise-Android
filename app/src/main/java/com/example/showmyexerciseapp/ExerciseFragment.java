package com.example.showmyexerciseapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;

import Components.Activity;
import Components.SavePostInterface;
import pl.droidsonroids.gif.GifImageView;

public class ExerciseFragment extends Fragment {

    ImageView image;
    TextView name;
    TextView instruction;
    TextView time;
    Button startBtn;
    Button doneBtn;
    Button pauseBtn;
    Activity activity;
    GifImageView gifImageView ;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image = view.findViewById(R.id.start_exercise_image);
        name = view.findViewById(R.id.start_exercise_name);
        instruction = view.findViewById(R.id.start_exercise_instruction);
        time = view.findViewById(R.id.start_exercise_time);
        startBtn = view.findViewById(R.id.start_exercise_btn);
        doneBtn = view.findViewById(R.id.start_exercise_done_btn);
        pauseBtn = view.findViewById(R.id.start_exercise_pause_btn);
        gifImageView = view.findViewById(R.id.gifImageView);

        // get values from previous activity
        Bundle bundle = getArguments();

        // init activity for user activity
        activity = new Activity();
        activity.setId(bundle.getInt("id"));
        activity.setExerciseName(bundle.getString("name"));

        image.setImageResource(bundle.getInt("image"));
        name.setText(activity.getExerciseName());
        instruction.setText(bundle.getString("instruction"));
        gifImageView.setImageResource(bundle.getInt("gif"));

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide start button
                // show done button and pause button
                startBtn.setVisibility(View.INVISIBLE);
                doneBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);

                // save start time to startTime
                startExercise();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finalize totalTime and send the all exercise data to database
                // navigate to next activity
                endExerciseTime();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show start button
                // hide done button and pause button
                startBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.INVISIBLE);

                // calculate workout time and store it to totalTime
                pauseExercise();
            }
        });
    }

    private void startExercise() {
        // set start time and show the time
        // if resume exercise, only set start time
        if(activity.getStartedAt() == null) {
            activity.initActivityTime(LocalDateTime.now());
            time.setText(activity.getStartedAt().toString());
            time.setVisibility(View.VISIBLE);
        } else {
            activity.setRestartedAt(LocalDateTime.now());
        }
    }

    private void pauseExercise() {
        // save workout time
        Duration duration = Duration.between(activity.getRestartedAt(), LocalDateTime.now());
        int diff = (int) Math.abs(duration.toMinutes());
        activity.setDuration(activity.getDuration() + diff);
    }

    private void endExerciseTime() {
        // calculate final result
        activity.setFinishedAt(LocalDateTime.now());
        Duration duration = Duration.between(activity.getRestartedAt(), activity.getFinishedAt());
        int diff = (int) Math.abs(duration.toMinutes());
        activity.setDuration(activity.getDuration() + diff);

        // record exercise data
        activity.saveActivity(getContext());
    }
}