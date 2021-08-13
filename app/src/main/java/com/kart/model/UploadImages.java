package com.kart.model;

import android.graphics.Bitmap;

public class UploadImages {
    private String image;
    private Bitmap bitmap;

    public UploadImages() {
    }

    public UploadImages(String image, Bitmap bm) {
        this.image = image;
        this.bitmap = bm;
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
