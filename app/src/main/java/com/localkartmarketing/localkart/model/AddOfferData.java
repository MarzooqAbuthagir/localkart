package com.localkartmarketing.localkart.model;

import android.graphics.Bitmap;

public class AddOfferData {
    private int offCount;
    private String heading;
    private String desc;
    private String image;
    private Bitmap bitmap;
    private String fromDate;
    private String toDate;

    public AddOfferData(String heading, String desc, String image, String fromDate, String toDate) {
        this.heading = heading;
        this.desc = desc;
        this.image = image;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public AddOfferData(String heading, String desc, String image) {
        this.heading = heading;
        this.desc = desc;
        this.image = image;
    }

    public AddOfferData(int count, String heading, String desc, String image, Bitmap bit) {
        this.offCount = count;
        this.heading = heading;
        this.desc = desc;
        this.image = image;
        this.bitmap = bit;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getOffCount() {
        return offCount;
    }

    public void setOffCount(int offCount) {
        this.offCount = offCount;
    }
}
