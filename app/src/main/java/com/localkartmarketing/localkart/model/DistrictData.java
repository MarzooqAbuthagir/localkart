package com.localkartmarketing.localkart.model;

public class DistrictData {
    String districtId;
    String districtName;

    public DistrictData() {
    }

    public DistrictData(String districtId, String districtName) {
        this.districtId = districtId;
        this.districtName = districtName;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
}
