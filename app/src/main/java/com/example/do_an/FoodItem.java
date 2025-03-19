package com.example.do_an;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String title;
    private String ingredients;
    private String steps;
    private String description;
    private String imageUrl;
    private String userId;
    private String cookingTime;
    private String postId;
    private String category; // ✅ Thêm thuộc tính category

    public FoodItem() {}

    public FoodItem(String title, String ingredients, String steps, String image, String userId, String cookingTime, String postId, String category) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = image;
        this.userId = userId;
        this.cookingTime = cookingTime;
        this.postId = postId;
        this.category = category; // ✅ Khởi tạo category
    }

    // Getter và Setter cho category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Các Getter & Setter khác
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getImage() {
        return imageUrl;
    }

    public void setImage(String image) {
        this.imageUrl = image;
    }

    public String getUserId() {
        return userId;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
