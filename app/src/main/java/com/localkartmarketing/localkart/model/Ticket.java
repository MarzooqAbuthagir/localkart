package com.localkartmarketing.localkart.model;

public class Ticket {
    private String name;
    private String amount;
    private String total;
    private String qty;
    private String description;
    public Ticket(String name, String amount, String total, String qty, String description) {
        this.name = name;
        this.amount = amount;
        this.total = total;
        this.qty = qty;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
