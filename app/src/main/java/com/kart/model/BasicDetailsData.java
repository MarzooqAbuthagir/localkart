package com.kart.model;

public class BasicDetailsData {
    private String businessType;
    private String businessName;
    private String categoryId;
    private String subCategoryId;
    private String logo;
    private String desc;

    public BasicDetailsData(String businessType, String businessName, String categoryId, String subCategoryId, String logo, String description) {
        this.businessType = businessType;
        this.businessName = businessName;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.logo = logo;
        this.desc = description;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}