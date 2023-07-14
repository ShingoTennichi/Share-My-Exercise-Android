package com.example.showmyexerciseapp;

public class Post {
    private int authorId;
    private String imageUrl;
    private String text;

    public int getAuthorId() {
        return authorId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setText(String text) {
        this.text = text;
    }
}
