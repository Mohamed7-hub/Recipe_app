package com.example.shoping;

public class MainRecipe {
    private String id;
    private String title;
    private String author;
    private String imageUrl;
    private String category;
    private String cookingTime;
    private float rating;
    private String cuisine;
    private boolean isPopular;

    public MainRecipe() {}

    public MainRecipe(String id, String title, String author, String imageUrl,
                      String category, String cookingTime, float rating,
                      String cuisine, boolean isPopular) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.category = category;
        this.cookingTime = cookingTime;
        this.rating = rating;
        this.cuisine = cuisine;
        this.isPopular = isPopular;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCookingTime() { return cookingTime; }
    public void setCookingTime(String cookingTime) { this.cookingTime = cookingTime; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }
}

