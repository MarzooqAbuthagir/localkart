package com.localkartmarketing.localkart.model;

public class EventListData {
    private String id;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String startTime;
    private String endTime;
    private String address;
    private String district;
    private String image;

    public EventListData(String id, String eventname, String date, String time1, String time2, String address, String district, String image) {
        this.id = id;
        this.eventName = eventname;
        this.eventDate = date;
        this.startTime = time1;
        this.endTime = time2;
        this.address = address;
        this.district = district;
        this.image = image;

    }

    public EventListData(String id, String event_id, String eventname, String date, String time1, String time2, String address, String district, String image) {
        this.id = id;
        this.eventId = event_id;
        this.eventName = eventname;
        this.eventDate = date;
        this.startTime = time1;
        this.endTime = time2;
        this.address = address;
        this.district = district;
        this.image = image;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
