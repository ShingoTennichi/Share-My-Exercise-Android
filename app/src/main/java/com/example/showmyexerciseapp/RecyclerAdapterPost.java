package com.example.showmyexerciseapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import Components.CustomFirebase;
import Components.Post;

public class RecyclerAdapterPost extends RecyclerView.Adapter<RecyclerAdapterPost.ViewHolder> {

    Context context;
    ArrayList<Post> posts;

    public RecyclerAdapterPost(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public RecyclerAdapterPost.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout,viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterPost.ViewHolder viewHolder, int position) {
        Post post = posts.get(position);
        viewHolder.time.setText(post.getCreatedAt());
        viewHolder.userName.setText(post.getAuthor());
        viewHolder.content.setText(post.getText());

        // retrieve and set image
        if(post.getImgUrl().equals("null")) {
            viewHolder.postImage.setVisibility(View.GONE);
        } else {
            CustomFirebase customFirebase = new CustomFirebase(context);
            customFirebase.getImage(viewHolder.postImage, post.getImgUrl());
        }

        viewHolder.like.setText("LIKE " + post.getLike());
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setLike(post.getLike()+1);
                viewHolder.like.setText("LIKE " + post.getLike());
                post.updateLike(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ConstraintLayout postContainer;
        TextView userName;
        TextView content;
        ImageView postImage;
        Button comment;
        Button like;
        TextView time;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = view.findViewById(R.id.post_layout);
            postContainer = view.findViewById(R.id.postContainer);
            userName = view.findViewById(R.id.postUserName);
            content = view.findViewById(R.id.postContent);
            postImage = view.findViewById(R.id.postImage);
            comment = view.findViewById(R.id.postCommentBtn);
            like = view.findViewById(R.id.postLikeBtn);
            time = view.findViewById(R.id.postTime);
        }
    }
}
