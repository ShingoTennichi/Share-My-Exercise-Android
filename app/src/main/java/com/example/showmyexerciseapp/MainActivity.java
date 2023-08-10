package com.example.showmyexerciseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;

import com.example.showmyexerciseapp.databinding.ActivityMainBinding;

import Components.Navigate;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // default fragment
        Navigate.navigateToNextFragment(MainActivity.this, new MainFragment());

        // handling fragments
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bt_nav_home) {
                Navigate.navigateToNextFragment(MainActivity.this, new MainFragment());
            } else if (itemId == R.id.bt_nav_post) {
                Navigate.navigateToNextFragment(MainActivity.this, new ShareFragment());
            } else if (itemId == R.id.bt_nav_share) {
                Navigate.navigateToNextFragment(MainActivity.this, new NewPostFragment());
            } else if (itemId == R.id.bt_nav_profile) {
                Navigate.navigateToNextFragment(MainActivity.this, new ProfileFragment());
            }
            return true;
        });
    }
}