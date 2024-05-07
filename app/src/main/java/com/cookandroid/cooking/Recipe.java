package com.cookandroid.cooking;
public class Recipe {
    private String title;
    private String recipe;
    private String userId;
    private String imageUrl;

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String title, String recipe, String userId, String imageUrl) {
        this.title = title;
        this.recipe = recipe;
        this.userId = userId;
        this.imageUrl = imageUrl;
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
}
