package com.kart.adapter;

import android.graphics.Bitmap;

public class OfferData {

    private String offerIndexId;
    private String heading;
    private String desc;
    private String image;
    private Bitmap bitmap;
    private String base64Str;

    public OfferData(String offerIndexId, String heading, String desc, String image, Bitmap bitmap, String base64Str) {
        this.offerIndexId = offerIndexId;
        this.heading = heading;
        this.desc = desc;
        this.image = image;
        this.bitmap = bitmap;
        this.base64Str = base64Str;
    }

    public String getBase64Str() {
        return base64Str;
    }

    public void setBase64Str(String base64Str) {
        this.base64Str = base64Str;
    }

    public String getOfferIndexId() {
        return offerIndexId;
    }

    public void setOfferIndexId(String offerIndexId) {
        this.offerIndexId = offerIndexId;
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
}
