package com.vtca.mytravels.entity;

import android.graphics.Bitmap;

import java.util.Objects;

public class SuggestLocation {
    private String placeID;
    private double placeLat;
    private double placeLng;
    private String formatedAddress;
    private String Name;
    private double rating;
    private int userRatingTotal;
    private Bitmap photo;

    public SuggestLocation(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(double placeLat) {
        this.placeLat = placeLat;
    }

    public double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(double placeLng) {
        this.placeLng = placeLng;
    }

    public String getFormatedAddress() {
        return formatedAddress;
    }

    public void setFormatedAddress(String formatedAddress) {
        this.formatedAddress = formatedAddress;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getUserRatingTotal() {
        return userRatingTotal;
    }

    public void setUserRatingTotal(int userRatingTotal) {
        this.userRatingTotal = userRatingTotal;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuggestLocation)) return false;
        SuggestLocation that = (SuggestLocation) o;
        return Objects.equals(placeID, that.placeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeID);
    }
}
