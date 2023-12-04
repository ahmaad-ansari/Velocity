package com.example.carmarketplaceapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostStepOneFragment extends PostStepBaseFragment {

    // Declaring UI elements
    private AutoCompleteTextView autocompleteCarMake, autocompleteCarModel, autocompleteTransmissionType, autocompleteDrivetrainType, autocompleteFuelType;
    private EditText editTextCarYear;
    private String[] carMakes, carModels, transmissionTypes, drivetrainTypes, fuelTypes;
    private View view;

    // Constants for image handling
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int MAX_IMAGES = 10;

    // Variables for image handling and display
    private String currentPhotoPath;
    private ImageAdapter imageAdapter;
    private ArrayList<String> imageUrls;
    RecyclerView recyclerViewMedia;
    private SharedViewModel viewModel;
    private CarListModel carModel;

    // List to store image sources (either Uri or String)
    private ArrayList<Object> imageSources;

    // Constructor
    public PostStepOneFragment() {
        // Required empty public constructor
    }

    // Resuming the fragment
    @Override
    public void onResume() {
        super.onResume();
        initializeViews(view); // Initializing views
        carModel = viewModel.getCarListModel().getValue();

        // Load existing data for editing if available
        if (carModel != null && carModel.getCarId() != null && !carModel.getCarId().isEmpty()) {
            loadCarData(carModel);
        }
    }

    // Creating the view for the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_step_one, container, false);

        // Initializing image section of the fragment
        initializeImageSection(view);

        // Observing changes in image sources and updating the adapter
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getImageSources().observe(getViewLifecycleOwner(), new Observer<List<Object>>() {
            @Override
            public void onChanged(List<Object> sources) {
                // Extracting image paths from the sources
                List<String> imagePaths = new ArrayList<>();
                for (Object source : sources) {
                    if (source instanceof Uri) {
                        // If it's a Uri, convert to string and add to the list
                        imagePaths.add(((Uri) source).toString());
                    } else if (source instanceof String) {
                        // If it's already a String (URL), add to the list
                        imagePaths.add((String) source);
                    }
                }
                // Updating the adapter with the new list of image paths
                imageAdapter.updateImagePaths(imagePaths);
                imageAdapter.notifyDataSetChanged();
            }
        });

        // Hiding the 'Previous' button for this step
        Button btnPrevious = view.findViewById(R.id.button_previous);
        btnPrevious.setVisibility(View.GONE);

        return view;
    }


    private void loadCarData(CarListModel carModel) {
        // Load data into AutoCompleteTextViews and EditTexts
        autocompleteCarMake.setText(carModel.getMake(), false);
        autocompleteCarModel.setText(carModel.getModel(), false);
        editTextCarYear.setText(String.valueOf(carModel.getYear()));
        autocompleteTransmissionType.setText(carModel.getTransmissionType(), false);
        autocompleteDrivetrainType.setText(carModel.getDrivetrainType(), false);
        autocompleteFuelType.setText(carModel.getFuelType(), false);

        // Load images if any into the ViewModel's image sources
        if (carModel.getImageUrls() != null && !carModel.getImageUrls().isEmpty()) {
            viewModel.setImageSources(new ArrayList<>(carModel.getImageUrls()));
        }
    }

    private void initializeViews(View view) {
        // Initialize AutoCompleteTextViews and EditText
        autocompleteCarMake = view.findViewById(R.id.autocomplete_car_make);
        autocompleteCarModel = view.findViewById(R.id.autocomplete_car_model);
        editTextCarYear = view.findViewById(R.id.editText_car_year);
        autocompleteTransmissionType = view.findViewById(R.id.autocomplete_transmission_type);
        autocompleteDrivetrainType = view.findViewById(R.id.autocomplete_drivetrain);
        autocompleteFuelType = view.findViewById(R.id.autocomplete_fuel_type);

        // Get string arrays from resources
        carMakes = getResources().getStringArray(R.array.car_makes);
        transmissionTypes = getResources().getStringArray(R.array.transmission_types);
        drivetrainTypes = getResources().getStringArray(R.array.drivetrain_types);
        fuelTypes = getResources().getStringArray(R.array.fuel_types);

        // Initialize and set dropdown adapters for AutoCompleteTextViews
        carModels = getCarModelsForMake(0); // Assuming Toyota is the first make
        setDropdownAdapter(autocompleteCarMake, carMakes);
        setDropdownAdapter(autocompleteCarModel, carModels);
        setDropdownAdapter(autocompleteTransmissionType, transmissionTypes);
        setDropdownAdapter(autocompleteDrivetrainType, drivetrainTypes);
        setDropdownAdapter(autocompleteFuelType, fuelTypes);

        // Listen for changes in the car make selection to update car models dropdown
        autocompleteCarMake.setOnItemClickListener((parent, view1, position, id) -> {
            carModels = getCarModelsForMake(position);
            setDropdownAdapter(autocompleteCarModel, carModels);
        });
    }


    private void setDropdownAdapter(AutoCompleteTextView autoCompleteTextView, String[] data) {
        // Create and set an ArrayAdapter for the AutoCompleteTextView with provided data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, data);
        autoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Filter the adapter if text is not empty
        if (!TextUtils.isEmpty(autoCompleteTextView.getText().toString())) {
            adapter.getFilter().filter(null);
        }
    }

    // Function to get car models based on selected make position
    private String[] getCarModelsForMake(int makePosition) {
        String[] models;
        switch (makePosition) {
            case 0: // Toyota
                models = getResources().getStringArray(R.array.toyota_models);
                break;
            case 1: // Honda
                models = getResources().getStringArray(R.array.honda_models);
                break;
            case 2: // BMW
                models = getResources().getStringArray(R.array.bmw_models);
                break;
            case 3: // Lexus
                models = getResources().getStringArray(R.array.lexus_models);
                break;
            case 4: // Nissan
                models = getResources().getStringArray(R.array.nissan_models);
                break;
            default:
                models = new String[0]; // Empty array if no match
        }
        return models;
    }


    private void initializeImageSection(View view) {
        // Initialize RecyclerView for displaying images
        recyclerViewMedia = view.findViewById(R.id.recyclerView_media);
        imageSources = new ArrayList<>(); // Changed to Object type
        imageAdapter = new ImageAdapter(getContext(), imageSources, viewModel);

        recyclerViewMedia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMedia.setAdapter(imageAdapter);

        // Button to add images
        Button btnAddImage = view.findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(v -> showImagePickOptions());

        // Initialize the RecyclerView with placeholders
        initializeRecyclerViewWithPlaceholders(); // Now this can be called safely
    }

    // Show options to pick images from Gallery or Camera
    private void showImagePickOptions() {
        CharSequence[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openGallery();
            } else if (which == 1) {
                captureImage();
            }
        });
        builder.show();
    }

    // Initialize the RecyclerView with placeholders for images
    private void initializeRecyclerViewWithPlaceholders() {
        for (int i = 0; i < MAX_IMAGES; i++) {
            imageSources.add(""); // Uri.EMPTY is used as a placeholder
        }
        if (imageAdapter != null) {
            imageAdapter.notifyDataSetChanged();
        }
    }

    // Open Gallery to select images
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); // Restrict to local content
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Start from Pictures directory
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void captureImage() {
        // Check if CAMERA permission is granted
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // If not, request the CAMERA permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // If permission is granted, start the camera intent
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        // Create an intent to capture an image
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there's a camera app available to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Create a file to save the captured image
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle errors in creating the image file
                Log.e("PostStepOneFragment", "Error creating image file", ex);
                return;
            }

            // If the file was successfully created, proceed to capture the image
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.carmarketplaceapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create a unique image file name based on timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the directory where images will be saved
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create a temporary image file
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save the file path for later use
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Check the request code to determine the source of the result
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Result from selecting image(s) from gallery
                handleGalleryResult(data);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Result from capturing image using the camera
                handleCameraResult();
            }
        }
    }

    private void handleGalleryResult(Intent data) {
        if (data != null && data.getClipData() != null) {
            // Multiple images selected from gallery
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                addImageSourceToList(imageUri);
            }
        } else if (data != null && data.getData() != null) {
            // Single image selected from gallery
            Uri imageUri = data.getData();
            addImageSourceToList(imageUri);
        }
    }

    private void handleCameraResult() {
        // Image captured from the camera
        Uri imageUri = Uri.fromFile(new File(currentPhotoPath));
        addImageSourceToList(imageUri);
    }

    private void addImageSourceToList(Object imageSource) {
        if (imageSource instanceof Uri) {
            // If the image source is a Uri (from gallery or camera)
            Uri imageUri = (Uri) imageSource;
            if (imageSources.size() < MAX_IMAGES) {
                imageSources.add(0, imageUri); // Add Uri to the front of the list
            } else {
                // Handle when the list is full
                // You might want to decide how to handle this scenario, e.g., remove the last element before adding a new one
                imageSources.remove(imageSources.size() - 1);
                imageSources.add(0, imageUri);
            }
        } else {
            // Handling other types of image sources (like placeholders or other types of URIs)
            int placeholderIndex = imageSources.indexOf(""); // Assuming "" is a placeholder
            if (placeholderIndex != -1) {
                imageSources.set(placeholderIndex, imageSource);
            } else {
                if (imageSources.size() < MAX_IMAGES) {
                    imageSources.add(imageSource);
                }
            }
        }

        // Notify the adapter of changes in the image list
        imageAdapter.notifyDataSetChanged();
        // Update the ViewModel with the updated list of image sources
        viewModel.setImageSources(new ArrayList<>(imageSources));
    }


    @Override
    protected void onNextClicked() {
        if (validateCurrentStep()) {
            // If all validations pass, navigate to the next step fragment
            PostFragment parentFragment = (PostFragment) getParentFragment();
            if (parentFragment != null) {
                parentFragment.goToNextStep(new PostStepTwoFragment());
            } else {
                // Handle case when the parent fragment is null
            }
        } else {
            // Show error or validation feedback
            Toast.makeText(getContext(), "Please check the input fields.", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean validateCurrentStep() {
        // Retrieve the current car model from ViewModel or create a new one if it doesn't exist
        carModel = viewModel.getCarListModel().getValue();
        if (carModel == null) {
            carModel = new CarListModel();
        }

        // Retrieve input values from the UI fields
        String make = autocompleteCarMake.getText().toString().trim();
        String model = autocompleteCarModel.getText().toString().trim();
        String yearString = editTextCarYear.getText().toString().trim();
        String transmissionType = autocompleteTransmissionType.getText().toString().trim();
        String drivetrainType = autocompleteDrivetrainType.getText().toString().trim();
        String fuelType = autocompleteFuelType.getText().toString().trim();

        // Perform validations for each field
        if (make.isEmpty()) {
            autocompleteCarMake.requestFocus();
            return false;
        }

        if (model.isEmpty()) {
            autocompleteCarModel.requestFocus();
            return false;
        }

        int year;
        if (yearString.isEmpty() || !yearString.matches("\\d{4}")) {
            editTextCarYear.requestFocus();
            return false;
        } else {
            try {
                year = Integer.parseInt(yearString);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year < 1900 || year > currentYear) {
                    editTextCarYear.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                editTextCarYear.requestFocus();
                return false;
            }
        }

        if (transmissionType.isEmpty()) {
            autocompleteTransmissionType.requestFocus();
            return false;
        }

        if (drivetrainType.isEmpty()) {
            autocompleteDrivetrainType.requestFocus();
            return false;
        }

        if (fuelType.isEmpty()) {
            autocompleteFuelType.requestFocus();
            return false;
        }

        // Set owner details to the car model (function setOwnerDetailsToListing() not provided)
        setOwnerDetailsToListing(carModel);

        // If all validations pass, set values to carModel
        carModel.setMake(make);
        carModel.setModel(model);
        carModel.setYear(year);
        carModel.setTransmissionType(transmissionType);
        carModel.setDrivetrainType(drivetrainType);
        carModel.setFuelType(fuelType);

        // Extract URLs from imageSources and set them to carModel
        List<String> urls = new ArrayList<>();
        for (Object imageSource : imageSources) {
            if (imageSource instanceof String) {
                urls.add((String) imageSource);
            }
        }
        carModel.setImageUrls(urls);

        // Update ViewModel with the modified car model
        viewModel.setCarListModel(carModel);

        // If all validations pass
        return true;
    }

    private void setOwnerDetailsToListing(CarListModel carModel) {
        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // If the current user exists, retrieve the Firebase Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = currentUser.getUid();

            // Create a reference to the user's document in the Firestore "users" collection
            DocumentReference userRef = db.collection("users").document(userId);

            // Fetch user details from Firestore
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Retrieve user details from the document snapshot
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String email = documentSnapshot.getString("email");
                    String userPhoneNumber = documentSnapshot.getString("phoneNumber");

                    // Format the user's phone number as (XXX) XXX-XXXX
                    String formattedPhoneNumber = String.format("(%s) %s-%s",
                            userPhoneNumber.substring(0, 3),
                            userPhoneNumber.substring(3, 6),
                            userPhoneNumber.substring(6));

                    // Set the owner details to the CarListModel
                    carModel.setOwnerName(firstName + " " + lastName);
                    carModel.setOwnerEmail(email);
                    carModel.setOwnerContactNumber(formattedPhoneNumber);
                } else {
                    // Document does not exist
                    Log.d("TAG", "No such document");
                }
            }).addOnFailureListener(e -> {
                // Task failed with an exception
                Log.d("TAG", "Error getting user details: ", e);
            });
        }
    }
}
