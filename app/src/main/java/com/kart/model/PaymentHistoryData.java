package com.kart.model;

public class PaymentHistoryData {
    private String packageName;
    private String paymentDate;
    private String validity;
    private String indexId;
    private String planType;
    private String amount;
    private String status;

    public PaymentHistoryData(String packageName, String paymentDate, String validity, String indexId, String planType, String amount, String status) {
        this.packageName = packageName;
        this.paymentDate = paymentDate;
        this.validity = validity;
        this.indexId = indexId;
        this.planType = planType;
        this.amount = amount;
        this.status = status;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
