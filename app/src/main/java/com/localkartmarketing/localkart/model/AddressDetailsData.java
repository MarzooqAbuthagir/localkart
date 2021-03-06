package com.localkartmarketing.localkart.model;

public class AddressDetailsData {
    private String doorNo = "";
    private String locality = "";
    private String area = "";
    private String post = "";
    private String landmark = "";
    private String pinCode = "";
    private String stateId = "";
    private String districtId = "";
    private String state = "";
    private String district = "";

    public AddressDetailsData(String doorNo, String locality, String area, String post, String landmark, String pinCode, String stateId, String districtId, String state, String district) {
        this.doorNo = doorNo;
        this.locality = locality;
        this.area = area;
        this.post = post;
        this.landmark = landmark;
        this.pinCode = pinCode;
        this.stateId = stateId;
        this.districtId = districtId;
        this.state = state;
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDoorNo() {
        return doorNo;
    }

    public void setDoorNo(String doorNo) {
        this.doorNo = doorNo;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }
}
