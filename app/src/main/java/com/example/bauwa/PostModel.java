package com.example.bauwa;

public class PostModel {

    String userID, status, postTime, postDate, postDescription, image, currentLocation, locationLatitude, locationLongitude, contact,
            openTime, shop, postType;

    public PostModel() {

    }

    public PostModel(String userID, String status, String postTime, String postDate, String postDescription, String image, String currentLocation, String locationLatitude, String locationLongitude, String contact, String openTime, String shop, String postType) {
        this.userID = userID;
        this.status = status;
        this.postTime = postTime;
        this.postDate = postDate;
        this.postDescription = postDescription;
        this.image = image;
        this.currentLocation = currentLocation;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.contact = contact;
        this.openTime = openTime;
        this.shop = shop;
        this.postType = postType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }
}
