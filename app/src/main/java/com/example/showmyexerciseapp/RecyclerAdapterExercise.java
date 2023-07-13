package com.example.showmyexerciseapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterExercise extends RecyclerView.Adapter<RecyclerAdapterExercise.ViewHolder> {

    Context context;
    ArrayList<Exercise> exercises;

    public RecyclerAdapterExercise(Context context) {
        this.context = context;
        initExerciseData();
    }

    @NonNull
    @Override
    public RecyclerAdapterExercise.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_exercise_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterExercise.ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.itemImage.setImageResource(exercise.getImageSmall());
        holder.itemName.setText(exercise.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", exercise.getId());
                bundle.putInt("image", exercise.getImageLarge());
                bundle.putString("name", exercise.getName());
                bundle.putString("instruction", exercise.getInstruction());

                ExerciseFragment exerciseFragment = new ExerciseFragment();
                exerciseFragment.setArguments(bundle);

                ((FragmentActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, exerciseFragment).addToBackStack(null)
                    .commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView itemImage;
        TextView itemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.main_exercise_layout);
            itemImage = itemView.findViewById(R.id.main_img_exercise);
            itemName = itemView.findViewById(R.id.main_exercise_name);
        }
    }

    private void initExerciseData() {
        exercises = new ArrayList<>();
        int[] smallImages = {
                R.drawable.sit_up_crop, R.drawable.push_up_crop, R.drawable.plank_crop,
                R.drawable.squat_crop, R.drawable.lunge_crop, R.drawable.jogging_crop
        };

        int[] largeImages = {
                R.drawable.sit_up, R.drawable.push_up, R.drawable.plank,
                R.drawable.squat, R.drawable.lunge, R.drawable.jogging
        };
        String[] names = {
            "Sit-up", "Push-up", "Plank", "Squat", "Lunge", "Jogging"
        };
        String[] instructions = {
            "instructionOfSit-up", "instructionOfPush-up", "instructionOfPlank",
            "instructionOfSquat", "instructionOfLunge", "instructionOfJogging"
        };

        for(int index = 0; index < names.length; index++) {
            Exercise exercise = new Exercise(
                index+1, smallImages[index], largeImages[index], names[index], instructions[index]
            );
            exercises.add(exercise);
        }
    };
}