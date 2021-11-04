package com.kart.model;

import android.graphics.Bitmap;

public class UploadImages {
    private String image;
    private Bitmap bitmap;
    private String imageIndexId;

    public UploadImages() {
    }

    public UploadImages(String image, Bitmap bm, String imageId) {
        this.image = image;
        this.bitmap = bm;
        this.imageIndexId = imageId;
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

    public String getImageIndexId() {
        return imageIndexId;
    }

    public void setImageIndexId(String imageIndexId) {
        this.imageIndexId = imageIndexId;
    }
}
