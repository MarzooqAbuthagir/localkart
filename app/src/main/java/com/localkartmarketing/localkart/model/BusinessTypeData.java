package com.localkartmarketing.localkart.model;

public class BusinessTypeData {
    private String businessId;
    private String businessName;

    public BusinessTypeData() {
    }

    public BusinessTypeData(String businessId, String businessName) {
        this.businessId = businessId;
        this.businessName = businessName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
