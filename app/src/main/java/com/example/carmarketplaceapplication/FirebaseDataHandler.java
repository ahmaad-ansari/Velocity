package com.example.carmarketplaceapplication;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseDataHandler {

    public interface SaveDataCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    public void saveCarListing(CarListModel carModel, SaveDataCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Step 1: Upload images and get URLs
        uploadImages(carModel.getImageUrls(), storageRef, new ImageUploadCallback() {
            @Override
            public void onUploadSuccess(List<String> imageUrls) {
                // Step 2: Save car data with image URLs to Firestore
                saveCarDataToFirestore(carModel, imageUrls, callback);
            }

            @Override
            public void onUploadFailure(Exception exception) {
                callback.onFailure(exception);
            }
        });
    }

    public interface UpdateDataCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    public void updateCarListing(CarListModel carModel, UpdateDataCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        List<String> imageUrls = carModel.getImageUrls();
        boolean needsUpload = false;

        for (String url : imageUrls) {
            // Check if URL is a local file path that needs uploading
            if (url != null && !url.startsWith("https://firebasestorage.googleapis.com")) {
                needsUpload = true;
                break;
            }
        }

        if (needsUpload) {
            // Proceed with re-uploading images
            uploadImages(imageUrls, storageRef, new ImageUploadCallback() {
                @Override
                public void onUploadSuccess(List<String> newImageUrls) {
                    updateCarDataInFirestore(carModel, newImageUrls, callback);
                }

                @Override
                public void onUploadFailure(Exception exception) {
                    callback.onFailure(exception);
                }
            });
        } else {
            // Use existing image URLs without uploading
            updateCarDataInFirestore(carModel, imageUrls, callback);
        }
    }

    private void updateCarDataInFirestore(CarListModel carModel, List<String> imageUrls, UpdateDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> carData = new HashMap<>();
        // Populate carData with all fields from carModel similar to saveCarDataToFirestore
        carData.put("make", carModel.getMake());
        carData.put("model", carModel.getModel());
        carData.put("year", carModel.getYear());
        carData.put("color", carModel.getColor());
        carData.put("odometer", carModel.getOdometer());
        carData.put("price", carModel.getPrice());
        carData.put("description", carModel.getDescription());
        carData.put("ownerName", carModel.getOwnerName());
        carData.put("ownerContactNumber", carModel.getOwnerContactNumber());
        carData.put("ownerEmail", carModel.getOwnerEmail());
        carData.put("ownerLocation", carModel.getOwnerLocation());
        carData.put("transmissionType", carModel.getTransmissionType());
        carData.put("drivetrainType", carModel.getDrivetrainType());
        carData.put("fuelType", carModel.getFuelType());
        carData.put("numberOfDoors", carModel.getNumberOfDoors());
        carData.put("numberOfSeats", carModel.getNumberOfSeats());
        carData.put("airConditioning", carModel.isAirConditioning());
        carData.put("navigationSystem", carModel.isNavigationSystem());
        carData.put("bluetoothConnectivity", carModel.isBluetoothConnectivity());

        carData.put("imageUrls", imageUrls); // Updated image URLs

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            carData.put("uid", userUid);

            String carId = carModel.getCarId();
            if (carId == null || carId.isEmpty()) {
                // Generate a new document ID if carId is null or empty
                carId = db.collection("cars").document().getId();
            } else {
                callback.onFailure(new Exception("Car ID is null or empty"));
            }
            db.collection("cars").document(carId)
                    .set(carData)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("User not signed in"));
        }
    }

    /*
    // Get the currently signed-in user's UID


    * */


    private void uploadImages(List<String> imageUrls, StorageReference storageRef, ImageUploadCallback callback) {
        // Check if the imageUris list is null or empty
        if (imageUrls == null || imageUrls.isEmpty()) {
            // No images to upload, so immediately call onUploadSuccess with an empty list or a predefined response
            callback.onUploadSuccess(new ArrayList<>()); // empty list
            return;
        }

        List<String> uploadedImageUrls = new ArrayList<>();
        AtomicInteger uploadCounter = new AtomicInteger(0);

        for (String url : imageUrls) {
            if (url != null && !url.isEmpty()) {
                String path = "images/" + UUID.randomUUID().toString() + ".jpg";
                StorageReference imageRef = storageRef.child(path);

                UploadTask uploadTask = imageRef.putFile(Uri.parse(url));
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        uploadedImageUrls.add(downloadUrl.toString());
                        if (uploadCounter.incrementAndGet() == imageUrls.size()) {
                            callback.onUploadSuccess(uploadedImageUrls);
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("UploadImages", "Error getting download URL", e);
                        callback.onUploadFailure(e);
                    });
                }).addOnFailureListener(e -> {
                    Log.e("UploadImages", "Upload failure", e);
                    callback.onUploadFailure(e);
                });
            } else {
//                uploadedImageUrls.add(""); // Add empty string for non-existent URIs
                if (uploadCounter.incrementAndGet() == imageUrls.size()) {
                    callback.onUploadSuccess(uploadedImageUrls);
                }
            }
        }
    }

    private void saveCarDataToFirestore(CarListModel carModel, List<String> imageUrls, SaveDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> carData = new HashMap<>();
        carData.put("make", carModel.getMake());
        carData.put("model", carModel.getModel());
        carData.put("year", carModel.getYear());
        carData.put("color", carModel.getColor());
        carData.put("odometer", carModel.getOdometer());
        carData.put("price", carModel.getPrice());
        carData.put("description", carModel.getDescription());
        carData.put("ownerName", carModel.getOwnerName());
        carData.put("ownerContactNumber", carModel.getOwnerContactNumber());
        carData.put("ownerEmail", carModel.getOwnerEmail());
        carData.put("ownerLocation", carModel.getOwnerLocation());
        carData.put("transmissionType", carModel.getTransmissionType());
        carData.put("drivetrainType", carModel.getDrivetrainType());
        carData.put("fuelType", carModel.getFuelType());
        carData.put("numberOfDoors", carModel.getNumberOfDoors());
        carData.put("numberOfSeats", carModel.getNumberOfSeats());
        carData.put("airConditioning", carModel.isAirConditioning());
        carData.put("navigationSystem", carModel.isNavigationSystem());
        carData.put("bluetoothConnectivity", carModel.isBluetoothConnectivity());

        carData.put("imageUrls", imageUrls); // Image URLs from Storage

        // Get the currently signed-in user's UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            carData.put("uid", userUid);

            String carId = carModel.getCarId();
            if (carId == null || carId.isEmpty()) {
                // Generate a new document ID if carId is null or empty
                carId = db.collection("cars").document().getId();
            }

            db.collection("cars").document(carId)
                    .set(carData)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("User not signed in"));
        }
    }

    private interface ImageUploadCallback {
        void onUploadSuccess(List<String> imageUrls);
        void onUploadFailure(Exception exception);
    }

    public interface FetchDataCallback {
        void onSuccess(List<CarListModel> carList);
        void onFailure(Exception exception);
    }

    public void fetchUserCarListings(FetchDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userUid = currentUser.getUid();

            db.collection("cars")
                    .whereEqualTo("uid", userUid) // Filter documents by the user's UID
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<CarListModel> userCarListings = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            CarListModel car = documentSnapshot.toObject(CarListModel.class);
                            car.setCarId(documentSnapshot.getId());
                            userCarListings.add(car);
                        }
                        callback.onSuccess(userCarListings);
                    })
                    .addOnFailureListener(callback::onFailure);
        } else {
            // User is not signed in, handle accordingly
            callback.onFailure(new Exception("User not signed in"));
        }
    }

    public void fetchFilteredCarListings(FilterBottomSheetFragment.FilterParams filterParams, FetchDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("cars");

        // Add conditions only for non-empty filter parameters
        if (!filterParams.make.isEmpty()) {
            query = query.whereEqualTo("make", filterParams.make);
        }
        if (!filterParams.model.isEmpty()) {
            query = query.whereEqualTo("model", filterParams.model);
        }
        if (!filterParams.transmission.isEmpty()) {
            query = query.whereEqualTo("transmissionType", filterParams.transmission);
        }
        if (!filterParams.drivetrain.isEmpty()) {
            query = query.whereEqualTo("drivetrainType", filterParams.drivetrain);
        }
        if (!filterParams.fuel.isEmpty()) {
            query = query.whereEqualTo("fuelType", filterParams.fuel);
        }
        if (filterParams.airConditioning) { // Assuming this is a Boolean
            query = query.whereEqualTo("airConditioning", filterParams.airConditioning);
        }
        if (filterParams.navigation) { // Assuming this is a Boolean
            query = query.whereEqualTo("navigationSystem", filterParams.navigation);
        }
        if (filterParams.bluetooth) { // Assuming this is a Boolean
            query = query.whereEqualTo("bluetoothConnectivity", filterParams.bluetooth);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<CarListModel> filteredList = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                CarListModel car = documentSnapshot.toObject(CarListModel.class);
                car.setCarId(documentSnapshot.getId());
                filteredList.add(car);
            }
            callback.onSuccess(filteredList);
        }).addOnFailureListener(callback::onFailure);
    }

    public void fetchCarListings(FetchDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cars")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CarListModel> carListings = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        CarListModel car = documentSnapshot.toObject(CarListModel.class);
                        car.setCarId(documentSnapshot.getId()); // Set the Firestore document ID
                        carListings.add(car);
                    }
                    callback.onSuccess(carListings);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
