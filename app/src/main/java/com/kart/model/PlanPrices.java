package com.kart.model;

public class PlanPrices {
    private String planId;
    private String planName;
    private String planPrice;
    private String planValidity;
    private boolean isCurrentPlan;

    public PlanPrices(String planId, String planName, String planPrice, String planValidity, boolean isCurrentPlan) {
        this.planId = planId;
        this.planName = planName;
        this.planPrice = planPrice;
        this.planValidity = planValidity;
        this.isCurrentPlan = isCurrentPlan;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public String getPlanValidity() {
        return planValidity;
    }

    public void setPlanValidity(String planValidity) {
        this.planValidity = planValidity;
    }

    public boolean isCurrentPlan() {
        return isCurrentPlan;
    }

    public void setCurrentPlan(boolean currentPlan) {
        isCurrentPlan = currentPlan;
    }
}
