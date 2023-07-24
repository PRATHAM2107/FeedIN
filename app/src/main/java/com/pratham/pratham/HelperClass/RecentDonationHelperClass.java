package com.pratham.feedin.HelperClass;

public class RecentDonationHelperClass {
    private String donatedFrom, foodImage, foodName, foodStatus, requestedOn;

    public RecentDonationHelperClass() {
    }

    public RecentDonationHelperClass(String donatedFrom , String foodImage , String foodName , String foodStatus , String requestedOn) {
        this.donatedFrom = donatedFrom;
        this.foodImage = foodImage;
        this.foodName = foodName;
        this.foodStatus = foodStatus;
        this.requestedOn = requestedOn;
    }

    public String getDonatedFrom() {
        return donatedFrom;
    }

    public void setDonatedFrom(String donatedFrom) {
        this.donatedFrom = donatedFrom;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodStatus() {
        return foodStatus;
    }

    public void setFoodStatus(String foodStatus) {
        this.foodStatus = foodStatus;
    }

    public String getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(String requestedOn) {
        this.requestedOn = requestedOn;
    }
}
