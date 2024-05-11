package com.cookandroid.cooking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.Serializable;

public class Recipe implements Serializable {
    private String title;
    private String recipe;
    private String userId;
    private String imageUrl;
    private String date; // 날짜 필드 추가

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String title, String recipe, String userId, String imageUrl, String date) {
        this.title = title;
        this.recipe = recipe;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getRecipe() {
        return recipe;
    }

    public String getUserId() {return userId;}

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() { return date;}
}
