package com.example.showmyexerciseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageView image;
    TextView name;
    TextView instruction;
    TextView time;
    Button startBtn;
    Button doneBtn;
    Button pauseBtn;

    Activity activity;
    LocalDateTime startTime;
    int totalTime = 0;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseFragment newInstance(String param1, String param2) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        // get values from previous activity
        Bundle bundle = getArguments();

        activity = new Activity(bundle.getInt("id"), bundle.getString("name"));

        image.setImageResource(bundle.getInt("image"));
        name.setText(activity.getName());
        instruction.setText(bundle.getString("instruction"));

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
        } else {
            activity.setRestartedAt(LocalDateTime.now());
        }
    }

    private void pauseExercise() {
        // testing
        //LocalDateTime sixMinutesAhead = startTime.plusDays(6);
        //Duration duration = Duration.between(startTime, sixMinutesAhead);

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
        try {
            activity.saveActivityData(getContext());
        } catch (Exception e) {
            Log.e("endExerciseTime", e.getMessage());
            Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }


        // bundle activity data to pass next activity
        Bundle bundle = new Bundle();
        bundle.putInt("time", activity.getDuration());
        WorkResultFragment workResultFragment = new WorkResultFragment();
        workResultFragment.setArguments(bundle);

        // navigate to Workout Result Fragment
        ((FragmentActivity) getContext())
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frame_layout, workResultFragment)
            .addToBackStack(null)
            .commit();
    }
}