package com.example.do_an;

public class SearchItem {
    private String foodName;
    private String imgUrl; // URL ảnh từ Firebase
    private int imgResId; // ID ảnh drawable (chỉ dùng khi dữ liệu cứng)

    public SearchItem() { } // Cần thiết cho Firebase

    // Constructor cho dữ liệu từ Firebase
    public SearchItem(String foodName, String imgUrl) {
        this.foodName = foodName;
        this.imgUrl = imgUrl;
    }

    // Constructor cho dữ liệu cứng (drawable resource)
    public SearchItem(String foodName, int imgResId) {
        this.foodName = foodName;
        this.imgResId = imgResId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }
}
