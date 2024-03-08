package com.localkartmarketing.localkart.model;

public class EventBookingList {
private String id;
private String name;
private String mobile;
private String date;
private String time;
private String qty;
    public EventBookingList(String id, String customer_name, String customer_mobile, String date, String time, String ticket_qty) {
        this.id
                 = id;
        this.name = customer_name;
        this.mobile = customer_mobile;
        this.date = date;
        this.time = time;
        this.qty = ticket_qty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
