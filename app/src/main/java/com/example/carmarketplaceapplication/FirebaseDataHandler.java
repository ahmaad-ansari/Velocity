package com.example.carmarketplaceapplication;

import android.net.Uri;
import android.util.Log;

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

    // Callback interface for saving data operations
    public interface SaveDataCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    /**
     * Method to save a car listing to Firebase.
     *
     * @param carModel The car listing model to be saved.
     * @param callback  The callback for success or failure in saving the car listing.
     */
    public void saveCarListing(CarListModel carModel, SaveDataCallback callback) {
        // Firebase Storage initialization
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

    // Interface for updating data
    public interface UpdateDataCallback {
        void onSuccess();
        void onFailure(Exception exception);
    }

    /**
     * Method to update a car listing in Firebase.
     *
     * @param carModel The car listing model to be updated.
     * @param callback  The callback for success or failure in updating the car listing.
     */
    public void updateCarListing(CarListModel carModel, UpdateDataCallback callback) {
        // Firebase Storage initialization
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        List<String> imageUrls = carModel.getImageUrls();
        updateCarDataInFirestore(carModel, imageUrls, callback);
    }

    /**
     * Updates car data in Firestore with the provided car model and image URLs.
     *
     * @param carModel   The CarListModel containing the car details to be updated.
     * @param imageUrls  The updated image URLs associated with the car listing.
     * @param callback   The callback to handle success or failure after updating.
     */
    private void updateCarDataInFirestore(CarListModel carModel, List<String> imageUrls, UpdateDataCallback callback) {
        // Firestore initialization
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> carData = new HashMap<>();

        // Populating carData with all fields from carModel
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

        // Setting updated image URLs
        carData.put("imageUrls", imageUrls);

        // Checking current user authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            carData.put("uid", userUid);

            String carId = carModel.getCarId();
            if (carId == null || carId.isEmpty()) {
                // Generate a new document ID if carId is null or empty
                carId = db.collection("cars").document().getId();
            } else {
                // Invoke onFailure callback if carId is invalid
                callback.onFailure(new Exception("Car ID is null or empty"));
            }

            // Saving car data to Firestore
            db.collection("cars").document(carId)
                    .set(carData)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        } else {
            // Invoke onFailure callback if user is not signed in
            callback.onFailure(new Exception("User not signed in"));
        }
    }

    /**
     * Deletes a car listing from Firestore based on the provided car ID.
     *
     * @param carId     The ID of the car listing to be deleted.
     * @param callback  The callback to handle success or failure after deletion.
     */
    public void deleteCarListing(String carId, SaveDataCallback callback) {
        // Firestore initialization
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (carId != null && !carId.isEmpty()) {
            // Deleting the specified car listing document
            db.collection("cars").document(carId)
                    .delete()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        } else {
            // Invoke onFailure callback if carId is invalid
            callback.onFailure(new Exception("Invalid car ID"));
        }
    }

    /**
     * Uploads images to Firebase Storage.
     *
     * @param imageUrls   The list of image URLs to be uploaded.
     * @param storageRef  The reference to the Firebase Storage.
     * @param callback    The callback to handle success or failure after image upload.
     */
    private void uploadImages(List<String> imageUrls, StorageReference storageRef, ImageUploadCallback callback) {
        // Check if the imageUrls list is null or empty
        if (imageUrls == null || imageUrls.isEmpty()) {
            // No images to upload, call onUploadSuccess with an empty list
            callback.onUploadSuccess(new ArrayList<>());
            return;
        }

        List<String> uploadedImageUrls = new ArrayList<>();
        AtomicInteger uploadCounter = new AtomicInteger(0);

        for (String url : imageUrls) {
            if (url != null && !url.isEmpty()) {
                // Generating a unique path for the image in Firebase Storage
                String path = "images/" + UUID.randomUUID().toString() + ".jpg";
                StorageReference imageRef = storageRef.child(path);

                // Uploading the image to Firebase Storage
                UploadTask uploadTask = imageRef.putFile(Uri.parse(url));
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Getting the download URL for the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        uploadedImageUrls.add(downloadUrl.toString());
                        if (uploadCounter.incrementAndGet() == imageUrls.size()) {
                            callback.onUploadSuccess(uploadedImageUrls);
                        }
                    }).addOnFailureListener(e -> {
                        // Handling failure to get download URL
                        Log.e("UploadImages", "Error getting download URL", e);
                        callback.onUploadFailure(e);
                    });
                }).addOnFailureListener(e -> {
                    // Handling image upload failure
                    Log.e("UploadImages", "Upload failure", e);
                    callback.onUploadFailure(e);
                });
            } else {
                // Adding an empty string for non-existent URIs
                if (uploadCounter.incrementAndGet() == imageUrls.size()) {
                    callback.onUploadSuccess(uploadedImageUrls);
                }
            }
        }
    }

    /**
     * Saves car data to Firestore along with image URLs.
     *
     * @param carModel   The CarListModel containing car details to be saved.
     * @param imageUrls  The image URLs associated with the car listing.
     * @param callback   The callback to handle success or failure after saving.
     */
    private void saveCarDataToFirestore(CarListModel carModel, List<String> imageUrls, SaveDataCallback callback) {
        // Firestore initialization
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> carData = new HashMap<>();

        // Populating carData with all fields from carModel
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

        // Setting image URLs from Storage
        carData.put("imageUrls", imageUrls);

        // Getting the currently signed-in user's UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            carData.put("uid", userUid);

            String carId = carModel.getCarId();
            if (carId == null || carId.isEmpty()) {
                // Generate a new document ID if carId is null or empty
                carId = db.collection("cars").document().getId();
            }

            // Saving car data to Firestore
            db.collection("cars").document(carId)
                    .set(carData)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        } else {
            // Invoke onFailure callback if user is not signed in
            callback.onFailure(new Exception("User not signed in"));
        }
    }

    /**
     * Callback interface for image upload success and failure.
     */
    private interface ImageUploadCallback {
        void onUploadSuccess(List<String> imageUrls);
        void onUploadFailure(Exception exception);
    }

    /**
     * Callback interface for fetching data success and failure.
     */
    public interface FetchDataCallback {
        void onSuccess(List<CarListModel> carList);
        void onFailure(Exception exception);
    }

    /**
     * Fetches car listings associated with the current user's UID.
     *
     * @param callback The callback to handle success or failure in fetching user's car listings.
     */
    public void fetchUserCarListings(FetchDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userUid = currentUser.getUid();

            db.collection("cars")
                    .whereEqualTo("uid", userUid) // Filtering documents by the user's UID
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
            // Handling the case where the user is not signed in
            callback.onFailure(new Exception("User not signed in"));
        }
    }

    /**
     * Fetches car listings based on filter parameters.
     *
     * @param filterParams The parameters used to filter the car listings.
     * @param callback     The callback to handle success or failure in fetching filtered car listings.
     */
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
        if (filterParams.airConditioning) {
            query = query.whereEqualTo("airConditioning", filterParams.airConditioning);
        }
        if (filterParams.navigation) {
            query = query.whereEqualTo("navigationSystem", filterParams.navigation);
        }
        if (filterParams.bluetooth) {
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

    /**
     * Fetches all car listings.
     *
     * @param callback The callback to handle success or failure in fetching all car listings.
     */
    public void fetchCarListings(FetchDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cars")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CarListModel> carListings = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        CarListModel car = documentSnapshot.toObject(CarListModel.class);
                        car.setCarId(documentSnapshot.getId());
                        carListings.add(car);
                    }
                    callback.onSuccess(carListings);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
