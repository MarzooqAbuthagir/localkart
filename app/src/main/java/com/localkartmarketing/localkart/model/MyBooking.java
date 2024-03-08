package com.localkartmarketing.localkart.model;

public class MyBooking {
    private String id;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventId;
    private String city;
    private String totalQty;
    private String image;

    public MyBooking(String id, String eventname, String date, String start_time, String event_id, String city, String total_qty, String image) {
        this.id = id;
        this.eventName = eventname;
        this.eventDate = date;
        this.eventTime = start_time;
        this.eventId = event_id;
        this.city = city;
        this.totalQty = total_qty;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setStartTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
