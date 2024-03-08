package com.localkartmarketing.localkart.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class EventTicket implements Parcelable {
    private String name;
    private String description;
    private String price;
    private int remaining;
    private int qty;

    public EventTicket(String name, String description, String price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public EventTicket(String name, String description, String price, int remaining, int qty) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.remaining = remaining;
        this.qty = qty;
    }

    public EventTicket(String name, String price, int qty) {
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    protected EventTicket(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readString();
        remaining = in.readInt();
        qty = in.readInt();
    }

    public static final Creator<EventTicket> CREATOR = new Creator<EventTicket>() {
        @Override
        public EventTicket createFromParcel(Parcel in) {
            return new EventTicket(in);
        }

        @Override
        public EventTicket[] newArray(int size) {
            return new EventTicket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeInt(remaining);
        parcel.writeInt(qty);
    }
}
