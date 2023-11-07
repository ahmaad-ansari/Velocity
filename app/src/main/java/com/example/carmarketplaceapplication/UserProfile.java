package com.example.carmarketplaceapplication;

public class UserProfile {
    private String uid;
    private String firstName;
    private String lastName;
    private String city;
    private String province;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;

    // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    public UserProfile() {
    }

    public UserProfile(String firstName, String lastName, String city, String province, String email, String phoneNumber, String profileImageUrl, String uid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.province = province;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.uid = uid;
    }

    // Getters and setters for each field
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Additional methods can be added here, such as a method to update the user profile
}

