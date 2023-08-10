package com.example.showmyexerciseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Components.Post;

public class ProfileFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Post> posts;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        getUserPosts();
        Toast.makeText(context, "Loading posts...", Toast.LENGTH_SHORT).show();

        recyclerView = view.findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        // empty posts data is passed to avoid "No adapter attached" error
        RecyclerAdapterPost recyclerAdapterPost = new RecyclerAdapterPost(context, posts);
        recyclerView.setAdapter(recyclerAdapterPost);
        recyclerAdapterPost.notifyDataSetChanged();
    }

    private void getUserPosts() {
        posts = new ArrayList<>();
        String requestPath = "/api/post/all-posts/user-posts";
        String url = BuildConfig.BASE_URL + requestPath;

        JSONObject json = new JSONObject();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("config_file", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            if(userId == -1) throw new Exception("user id is not found");
            json.put("id", userId);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            Log.e("Level1",data.getString("status"));
                            JSONObject userData = data.getJSONObject("result");
                            Log.e("Level2","passed");
                            JSONArray userPosts = userData.getJSONArray("userPosts");
                            Log.e("Level3","passed");
                            for(int index = 0; index < userPosts.length(); index++) {
                                Post postToBeMapped = new Post();
                                JSONObject post = (JSONObject) userPosts.get(index);
                                Log.e("Level4","passed");
                                postToBeMapped.setAuthor(userData.getString("author"));
                                Log.e("Level5","passed");
                                postToBeMapped.setImgUrl(post.getString("imgUrl"));
                                Log.e("Level6","passed");
                                postToBeMapped.setText(post.getString("text"));
                                Log.e("Level7","passed");
                                postToBeMapped.setLike(post.getInt("like"));
                                Log.e("Level8","passed");
                                postToBeMapped.setCreatedAt(post.getString("createdAt").substring(0,10));
                                Log.e("Level9","passed");
                                posts.add(postToBeMapped);
                            }
                            Log.e("Level10","passed");
                            // recyclerView will be updated after data fetching is successfully executed
                            RecyclerAdapterPost recyclerAdapterPost = new RecyclerAdapterPost(context, posts);
                            recyclerView.setAdapter(recyclerAdapterPost);
                            recyclerAdapterPost.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(context, "Something went wrong, try later", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("initPosts", "onError: " + error.getMessage());
                    }
                }
        );

        // execute the request
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
