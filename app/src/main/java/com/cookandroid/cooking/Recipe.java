package com.cookandroid.cooking;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String key;
    private String title;
    private String recipe;
    private String userId;
    private String imageUrl;
    private String date;
    private String userEmail;

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String title, String recipe, String userId, String imageUrl, String date, String userEmail) {
        this.title = title;
        this.recipe = recipe;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.date = date;
        this.userEmail = userEmail; // userEmail 초기화
    }

    public String getTitle() {
        return title;
    }

    public String getRecipe() {
        return recipe;
    }

    public String getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
