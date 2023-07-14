package com.example.showmyexerciseapp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPostFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView etPostText;
    ImageView uploadedImage;
    Button postBtn;
    Uri imageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public NewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
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
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        imageUri = data.getData();
                        uploadedImage.setImageURI(imageUri);
                    } else {
                        Toast.makeText(getContext(), "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Post post = new Post();
                // set post data
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("config_file", Context.MODE_PRIVATE);
                post.setAuthorId(sharedPreferences.getInt("userId", -1));
                post.setText(etPostText.getText().toString());

                // if an image is set, store to firebase storage
                if(imageUri != null) {
                    // set image
                    post.setImageUrl(UUID.randomUUID().toString());

                    // init firebase connection setting
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    // set mata data to send request
                    StorageReference imageRef = storageReference.child("images/" + post.getImageUrl());
                    imageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else if(post.getText().equals("")) {
                    // process end due to no input
                    Toast.makeText(getContext(), "text or image is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                // save post data as json object
                JSONObject json = new JSONObject();
                try {
                    json.put("authorId", post.getAuthorId());
                    json.put("imageUrl", post.getImageUrl());
                    json.put("text", post.getText());
                } catch (Exception e) {
                    Log.e("create JSONObject", "" + e.getMessage());
                }

                // create http request
                final String url = "http://10.0.2.2:3000/api/admin/post-table";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getContext(), "onResponse: posted", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "onErrorResponse: error", Toast.LENGTH_SHORT).show();
                        }
                    }
                );

                // execute the request
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
                Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                navigateToMainFragment();
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

    private void navigateToMainFragment() {
        // navigate to Workout Result Fragment
        ((FragmentActivity) getContext())
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frame_layout, new MainFragment())
            .addToBackStack(null)
            .commit();
    }
}