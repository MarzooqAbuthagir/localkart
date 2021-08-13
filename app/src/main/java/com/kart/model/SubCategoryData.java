package com.kart.model;

public class SubCategoryData {
    private String subCategoryName;
    private String subCategoryImage;
    private String subCategoryId;

    public SubCategoryData(String subCategoryName, String subCategoryImage, String subCategoryId) {
        this.subCategoryName = subCategoryName;
        this.subCategoryImage = subCategoryImage;
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryImage() {
        return subCategoryImage;
    }

    public void setSubCategoryImage(String subCategoryImage) {
        this.subCategoryImage = subCategoryImage;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }
}
