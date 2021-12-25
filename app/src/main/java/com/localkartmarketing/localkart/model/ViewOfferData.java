package com.localkartmarketing.localkart.model;

import java.util.List;

public class ViewOfferData {
    private String name;
    private String logo;
    private String distance;
    private AccessOptions accessOptions;
    private List<ShopBanner> multiImages;

    public ViewOfferData() {
    }

    public ViewOfferData(String name, String logo, String distance, AccessOptions accessOptions, List<ShopBanner> multiImages) {
        this.name = name;
        this.logo = logo;
        this.distance = distance;
        this.accessOptions = accessOptions;
        this.multiImages = multiImages;
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

    public List<ShopBanner> getMultiImages() {
        return multiImages;
    }

    public void setMultiImages(List<ShopBanner> multiImages) {
        this.multiImages = multiImages;
    }
}
