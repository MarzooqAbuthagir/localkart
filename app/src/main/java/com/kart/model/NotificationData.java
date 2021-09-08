package com.kart.model;

public class NotificationData {
    private String fromDate;
    private String toDate;
    private String postIndexId;
    private String shopIndexId;
    private String shopType;
    private String postType;
    private String postHeading;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getPostIndexId() {
        return postIndexId;
    }

    public void setPostIndexId(String postIndexId) {
        this.postIndexId = postIndexId;
    }

    public String getShopIndexId() {
        return shopIndexId;
    }

    public void setShopIndexId(String shopIndexId) {
        this.shopIndexId = shopIndexId;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostHeading() {
        return postHeading;
    }

    public void setPostHeading(String postHeading) {
        this.postHeading = postHeading;
    }
}
