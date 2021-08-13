package com.kart.model;

public class DirectoryData {
    private String name;
    private String logo;
    private String distance;
    private AccessOptions accessOptions;
    private String description;
    private String shopId;
    private String shopType;
    private String latitude;
    private String longitude;

    public DirectoryData() {
    }

    public DirectoryData(String name, String logo, String distance, AccessOptions accessOptions, String description, String shopId, String shopType, String latitude, String longitude) {
        this.name = name;
        this.logo = logo;
        this.distance = distance;
        this.accessOptions = accessOptions;
        this.description = description;
        this.shopId = shopId;
        this.shopType = shopType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public AccessOptions getAccessOptions() {
        return accessOptions;
    }

    public void setAccessOptions(AccessOptions accessOptions) {
        this.accessOptions = accessOptions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
