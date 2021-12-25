package com.localkartmarketing.localkart.model;

public class CategoryData {
    private String categoryName;
    private String categoryImage;
    private String categoryId;

    public CategoryData(String categoryName, String categoryId, String categoryImage) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
