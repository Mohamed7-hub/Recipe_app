package com.example.shoping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    private String id;
    private String title;
    private Object ingredients;
    private Object instructions;
    private String userId;
    private String userName;
    private String cookingTime;
    private String imageUrl;
    private int likes;
    private boolean isLiked;
    private boolean isBookmarked;

    public Recipe() {}

    public Recipe(String id, String title, Object ingredients, Object instructions,
                  String userId, String userName, String cookingTime, String imageUrl) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.userId = userId;
        this.userName = userName;
        this.cookingTime = cookingTime;
        this.imageUrl = imageUrl;
        this.likes = 0;
        this.isLiked = false;
        this.isBookmarked = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIngredients() {
        if (ingredients instanceof String) {
            return (String) ingredients;
        } else if (ingredients instanceof ArrayList) {
            return String.join("\n", (List<String>) ingredients);
        }
        return "";
    }
    public void setIngredients(Object ingredients) { this.ingredients = ingredients; }

    public String getInstructions() {
        if (instructions instanceof String) {
            return (String) instructions;
        } else if (instructions instanceof ArrayList) {
            return String.join("\n", (List<String>) instructions);
        }
        return "";
    }
    public void setInstructions(Object instructions) { this.instructions = instructions; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCookingTime() { return cookingTime; }
    public void setCookingTime(String cookingTime) { this.cookingTime = cookingTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }
}