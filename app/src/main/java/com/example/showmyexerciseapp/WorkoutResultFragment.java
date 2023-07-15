package com.example.showmyexerciseapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import Components.Navigate;

public class WorkoutResultFragment extends Fragment {

    TextView result;
    int time;
    Button homeBtn;
    Button shareBtn;

    public WorkoutResultFragment() {
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
        return inflater.inflate(R.layout.fragment_workout_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        result = view.findViewById(R.id.workout_result);
        homeBtn = view.findViewById(R.id.workout_result_home_btn);
        shareBtn = view.findViewById(R.id.workout_result_share_btn);

        // display the result
        Bundle bundle = getArguments();
        time = bundle.getInt("time");
        result.setText(getString(R.string.workout_result, time));

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigate.navigateToNextFragment(getContext(), new MainFragment());
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigate.navigateToNextFragment(getContext(), new NewPostFragment());
            }
        });
    }
}