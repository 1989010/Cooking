package com.cookandroid.cooking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Recipe {
    private String title;
    private String recipe;
    private String userId;
    private String imageUrl;
    private String registrationDate; // 등록된 날짜를 저장하는 필드 추가

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String title, String recipe, String userId, String imageUrl) {
        this.title = title;
        this.recipe = recipe;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.registrationDate = getCurrentDate(); // 현재 날짜로 설정
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

    public String getRegistrationDate() {
        return registrationDate;
    }

    // 현재 날짜를 문자열로 반환하는 메서드
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
