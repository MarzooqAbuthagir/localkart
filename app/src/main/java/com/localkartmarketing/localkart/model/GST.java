package com.localkartmarketing.localkart.model;

public class GST {
    private String label;
    private String percent;
    private String amount;
    public GST(String label, String percent, String amount) {
        this.label = label;
        this.percent = percent;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
