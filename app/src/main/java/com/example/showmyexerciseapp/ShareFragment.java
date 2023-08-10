package com.example.showmyexerciseapp;

import android.content.Context;
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

public class ShareFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Post> posts = new ArrayList<>();

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        Toast.makeText(context, "Loading posts...", Toast.LENGTH_SHORT).show();

        recyclerView = view.findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        // empty posts data is passed to avoid "No adapter attached" error
        RecyclerAdapterPost recyclerAdapterPost = new RecyclerAdapterPost(context, posts);
        recyclerView.setAdapter(recyclerAdapterPost);
        recyclerAdapterPost.notifyDataSetChanged();

        getPosts();
    }

    private void getPosts() {
        posts = new ArrayList<>();
        String requestPath = "/api/post/all-posts/with-author";
        String url = BuildConfig.BASE_URL + requestPath;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray userPosts = data.getJSONArray("result");
                            for(int index = 0; index < userPosts.length(); index++) {
                                Post postToBeMapped = new Post();
                                JSONObject post = (JSONObject) userPosts.get(index);
                                postToBeMapped.setPostId(post.getInt("id"));
                                postToBeMapped.setAuthor(post.getJSONObject("author").getString("firstName") + " " + post.getJSONObject("author").getString("lastName"));
                                postToBeMapped.setImgUrl(post.getString("imgUrl"));
                                postToBeMapped.setText(post.getString("text"));
                                postToBeMapped.setLike(post.getInt("like"));
                                postToBeMapped.setCreatedAt(post.getString("createdAt").substring(0,10));
                                posts.add(postToBeMapped);
                            }

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