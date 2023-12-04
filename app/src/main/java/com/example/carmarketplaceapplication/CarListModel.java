package com.example.carmarketplaceapplication;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class CarListModel implements Serializable {

    private String carId;
    private String make;
    private String model;
    private int year;
    private String color;
    private double odometer;
    private double price;
    private String description;
    private List<Uri> imageUris;
    private List<String> imageUrls;
    private List<Uri> videoUris;
    private String ownerName;
    private String ownerContactNumber;
    private String ownerEmail;
    private String ownerLocation;
    private String transmissionType;
    private String drivetrainType;
    private String fuelType;
    private String numberOfDoors;
    private String numberOfSeats;
    private boolean airConditioning;
    private boolean navigationSystem;
    private boolean bluetoothConnectivity;

    public CarListModel() {
        this.carId = null;
        this.make = null;
        this.model = null;
        this.year = 0;
        this.color = null;
        this.odometer = 0;
        this.price = 0;
        this.description = null;
        this.imageUris = null;
        this.imageUrls = null;
        this.videoUris = null;
        this.ownerName = null;
        this.ownerContactNumber = null;
        this.ownerEmail = null;
        this.ownerLocation = null;
        this.transmissionType = null;
        this.drivetrainType = null;
        this.fuelType = null;
        this.numberOfDoors = null;
        this.numberOfSeats = null;
        this.airConditioning = false;
        this.navigationSystem = false;
        this.bluetoothConnectivity = false;
    }
    public CarListModel(String carId, String make, String model, int year, String color, double odometer, double price, String description, List<Uri> imageUris, List<String> imageUrls,List<Uri> videoUris, String ownerName, String ownerContactNumber, String ownerEmail, String ownerLocation, String transmissionType, String drivetrainType, String fuelType, String numberOfDoors, String numberOfSeats, boolean airConditioning, boolean navigationSystem, boolean bluetoothConnectivity, boolean hasSunroof) {
        this.carId = carId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.odometer = odometer;
        this.price = price;
        this.description = description;
        this.imageUris = imageUris;
        this.imageUrls = imageUrls;
        this.videoUris = videoUris;
        this.ownerName = ownerName;
        this.ownerContactNumber = ownerContactNumber;
        this.ownerEmail = ownerEmail;
        this.ownerLocation = ownerLocation;
        this.transmissionType = transmissionType;
        this.drivetrainType = drivetrainType;
        this.fuelType = fuelType;
        this.numberOfDoors = numberOfDoors;
        this.numberOfSeats = numberOfSeats;
        this.airConditioning = airConditioning;
        this.navigationSystem = navigationSystem;
        this.bluetoothConnectivity = bluetoothConnectivity;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOwnerContactNumber(String ownerContactNumber) { this.ownerContactNumber = ownerContactNumber; }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setOwnerLocation(String ownerLocation) {
        this.ownerLocation = ownerLocation;
    }

    public void setTransmissionType(String transmissionType) { this.transmissionType = transmissionType; }

    public void setDrivetrainType(String drivetrainType) {
        this.drivetrainType = drivetrainType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setNumberOfDoors(String numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public void setNumberOfSeats(String numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public void setAirConditioning(boolean airConditioning) { this.airConditioning = airConditioning; }

    public void setNavigationSystem(boolean navigationSystem) { this.navigationSystem = navigationSystem; }

    public void setBluetoothConnectivity(boolean bluetoothConnectivity) { this.bluetoothConnectivity = bluetoothConnectivity; }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    public String getNumberOfDoors() {
        return numberOfDoors;
    }

    public String getNumberOfSeats() {
        return numberOfSeats;
    }

    public boolean isAirConditioning() {
        return airConditioning;
    }

    public boolean isNavigationSystem() {
        return navigationSystem;
    }

    public boolean isBluetoothConnectivity() {
        return bluetoothConnectivity;
    }

    @Override
    public String toString() {
        return "CarListing{" +
                "carId='" + carId + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", odometer=" + odometer +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imageUris=" + imageUris +
                ", imageUrls=" + imageUrls +
                ", videoUris=" + videoUris +
                ", ownerName='" + ownerName + '\'' +
                ", ownerContactNumber='" + ownerContactNumber + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", ownerLocation='" + ownerLocation + '\'' +
                ", transmissionType='" + transmissionType + '\'' +
                ", drivetrainType='" + drivetrainType + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", numberOfDoors=" + numberOfDoors +
                ", numberOfSeats=" + numberOfSeats +
                ", airConditioning=" + airConditioning +
                ", navigationSystem=" + navigationSystem +
                ", bluetoothConnectivity=" + bluetoothConnectivity +
                '}';
    }
}
