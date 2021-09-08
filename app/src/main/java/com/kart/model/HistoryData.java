package com.kart.model;

public class HistoryData {
    private String postIndexId;
    private String postDate;
    private String postCode;
    private String type;
    private String heading;
    private String count;
    private String shopType;
    private String shopIndexId;
    private String shopLatitude;
    private String shopLongitude;
    private String status;

    public HistoryData(String postIndexId, String postDate, String postCode, String type, String heading, String count, String shopType, String shopId, String shopLati, String shopLongi, String status) {
        this.postIndexId = postIndexId;
        this.postDate = postDate;
        this.postCode = postCode;
        this.type = type;
        this.heading = heading;
        this.count = count;
        this.shopType = shopType;
        this.shopIndexId = shopId;
        this.shopLatitude = shopLati;
        this.shopLongitude = shopLongi;
        this.status = status;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public String getShopIndexId() {
        return shopIndexId;
    }

    public void setShopIndexId(String shopIndexId) {
        this.shopIndexId = shopIndexId;
    }

    public String getShopLatitude() {
        return shopLatitude;
    }

    public void setShopLatitude(String shopLatitude) {
        this.shopLatitude = shopLatitude;
    }

    public String getShopLongitude() {
        return shopLongitude;
    }

    public void setShopLongitude(String shopLongitude) {
        this.shopLongitude = shopLongitude;
    }

    public String getPostIndexId() {
        return postIndexId;
    }

    public void setPostIndexId(String postIndexId) {
        this.postIndexId = postIndexId;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
