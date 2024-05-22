package com.cookandroid.cooking;

public class Post {
    private String title;
    private String userId;
    private String date;
    private String imageUrl;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String title, String userId, String date, String imageUrl) {
        this.title = title;
        this.userId = userId;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
