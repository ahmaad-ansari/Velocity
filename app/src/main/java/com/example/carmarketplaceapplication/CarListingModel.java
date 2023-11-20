package com.example.carmarketplaceapplication;

import java.util.List;

public class CarListingModel {

    private String carId;//=========================
    private String make;//
    private String model;//
    private int year;//
    private String color;//
    private double odometer;//
    private double price;//
    private String description;//
    private List<String> imageUrls;//=========================
    private List<String> videoUrls;//=========================
    private String ownerName;//=========================
    private String ownerContactNumber;//=========================
    private String ownerEmail;//=========================
    private String ownerLocation;//=========================
    private String transmissionType;//
    private String drivetrainType;//
    private String fuelType;//
    private int numberOfDoors;//
    private int numberOfSeats;//
    private boolean isAirConditioning;//
    private boolean hasNavigationSystem;//
    private boolean hasBluetoothConnectivity;//

    public CarListingModel(String carId, String make, String model, int year, String color, double odometer, double price, String description, List<String> imageUrls, List<String> videoUrls, String ownerName, String ownerContactNumber, String ownerEmail, String ownerLocation, String transmissionType, String drivetrainType, String fuelType, int numberOfDoors, int numberOfSeats, boolean isAirConditioning, boolean hasNavigationSystem, boolean hasBluetoothConnectivity, boolean hasSunroof) {
        this.carId = carId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.price = price;
        this.description = description;
        this.imageUrls = imageUrls;
        this.videoUrls = videoUrls;
        this.ownerName = ownerName;
        this.ownerContactNumber = ownerContactNumber;
        this.ownerEmail = ownerEmail;
        this.ownerLocation = ownerLocation;
        this.transmissionType = transmissionType;
        this.drivetrainType = drivetrainType;
        this.fuelType = fuelType;
        this.numberOfDoors = numberOfDoors;
        this.numberOfSeats = numberOfSeats;
        this.isAirConditioning = isAirConditioning;
        this.hasNavigationSystem = hasNavigationSystem;
        this.hasBluetoothConnectivity = hasBluetoothConnectivity;
    }

    public String getCarId() {
        return carId;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public double getOdometer() {
        return odometer;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public List<String> getVideoUrls() {
        return videoUrls;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerContactNumber() {
        return ownerContactNumber;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerLocation() {
        return ownerLocation;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public String getDrivetrainType() {
        return drivetrainType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public boolean isAirConditioning() {
        return isAirConditioning;
    }

    public boolean isHasNavigationSystem() {
        return hasNavigationSystem;
    }

    public boolean isHasBluetoothConnectivity() {
        return hasBluetoothConnectivity;
    }

}
