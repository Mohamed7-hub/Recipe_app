package com.example.shoping;

public class UserInteractions {
    private String recipeId;
    private String userId;
    private boolean liked;
    private boolean bookmarked;

    public UserInteractions() {
        // Required empty constructor for Firestore
    }

    public UserInteractions(String recipeId, String userId, boolean liked, boolean bookmarked) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.liked = liked;
        this.bookmarked = bookmarked;
    }

    // Getters and Setters
    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public boolean isBookmarked() { return bookmarked; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
}