package com.kart.model;

public class DealsOfferData {
    private String fromDate;
    private String toDate;
    private String name;
    private String logo;
    private String description;
    private String offerCount;
    private String distance;
    private AccessOptions accessOptions;
    private String postIndexId;
    private String festivalName;
    private String shopIndexId;
    private String type;
    private String latitude;
    private String longitude;

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

    public String getShopIndexId() {
        return shopIndexId;
    }

    public void setShopIndexId(String shopIndexId) {
        this.shopIndexId = shopIndexId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFestivalName() {
        return festivalName;
    }

    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName;
    }

    public String getPostIndexId() {
        return postIndexId;
    }

    public void setPostIndexId(String postIndexId) {
        this.postIndexId = postIndexId;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(String offerCount) {
        this.offerCount = offerCount;
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
}
