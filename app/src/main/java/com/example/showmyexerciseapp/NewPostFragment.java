package com.example.showmyexerciseapp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import Components.CustomFirebase;
import Components.Post;

public class NewPostFragment extends Fragment {

    Context context;
    TextView etPostText;
    ImageView uploadedImage;
    Button postBtn;
    Uri imgUri;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public NewPostFragment() {
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
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        etPostText = view.findViewById(R.id.etPostText);
        uploadedImage = view.findViewById(R.id.upload_image);
        postBtn = view.findViewById(R.id.post_btn);
        uploadedImage.setImageResource(R.drawable.baseline_add_a_photo_24);

        uploadedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to image selection display
                choosePicture();
            }
        });

        // code used for opening photo selection display
        // called in chooseImage()
        activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // if image is chosen, RESULT_OK
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        imgUri = data.getData();
                        uploadedImage.setImageURI(imgUri);
                    } else {
                        Toast.makeText(context, "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Post post = new Post();
                // set post data
                SharedPreferences sharedPreferences = context.getSharedPreferences("config_file", Context.MODE_PRIVATE);
                post.setAuthorId(sharedPreferences.getInt("userId", -1));
                post.setText(etPostText.getText().toString());

                // if an image is set, store to firebase storage
                if(imgUri != null) {
                    // use uuid as image name
                    String uuid = UUID.randomUUID().toString();
                    post.setImgUrl(uuid);

                    CustomFirebase customFirebase = new CustomFirebase(context);
                    customFirebase.uploadImage(imgUri, post.getImgUrl());

                } else if(post.getText().equals("")) {
                    // process end due to no input
                    Toast.makeText(context, "text or image is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // save post data to database
                post.savePost(context);
            }
        });
    }

    private void choosePicture() {
        // navigate to image selection display
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    };
}