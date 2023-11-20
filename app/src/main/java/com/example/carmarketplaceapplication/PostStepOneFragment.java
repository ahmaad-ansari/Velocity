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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostStepOneFragment extends PostStepBaseFragment  {

    private AutoCompleteTextView autocompleteCarMake, autocompleteCarModel, autocompleteTransmissionType, autocompleteDrivetrainType, autocompleteFuelType;
    private EditText editTextCarYear;
    private String[] carMakes, carModels, transmissionTypes, drivetrainTypes, fuelTypes;
    private View view;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private static final int MAX_IMAGES = 8;
    private String currentPhotoPath;

    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris;
    RecyclerView recyclerViewMedia;



    public PostStepOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeViews(view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_step_one, container, false);
        initializeImageSection(view);

        return view;
    }

    private void initializeViews(View view) {
        // Initialize AutoCompleteTextViews
        autocompleteCarMake = view.findViewById(R.id.autocomplete_car_make);
        autocompleteCarModel = view.findViewById(R.id.autocomplete_car_model);
        editTextCarYear = view.findViewById(R.id.editText_car_year);
        autocompleteTransmissionType = view.findViewById(R.id.autocomplete_transmission_type);
        autocompleteDrivetrainType = view.findViewById(R.id.autocomplete_drivetrain);
        autocompleteFuelType = view.findViewById(R.id.autocomplete_fuel_type);

        carMakes = getResources().getStringArray(R.array.car_makes);
        carModels = getResources().getStringArray(R.array.car_models);
        transmissionTypes = getResources().getStringArray(R.array.transmission_types);
        drivetrainTypes = getResources().getStringArray(R.array.drivetrain_types);
        fuelTypes = getResources().getStringArray(R.array.fuel_types);

        // Set adapters for the AutoCompleteTextViews
        setDropdownAdapter(autocompleteCarMake, carMakes);
        setDropdownAdapter(autocompleteCarModel, carModels);
        setDropdownAdapter(autocompleteTransmissionType, transmissionTypes);
        setDropdownAdapter(autocompleteDrivetrainType, drivetrainTypes);
        setDropdownAdapter(autocompleteFuelType, fuelTypes);

    }

    private void initializeImageSection(View view) {
        recyclerViewMedia = view.findViewById(R.id.recyclerView_media);
        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageUris);

        recyclerViewMedia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMedia.setAdapter(imageAdapter);

        Button btnAddImage = view.findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(v -> showImagePickOptions());

        initializeRecyclerViewWithPlaceholders();
    }

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

    private void initializeRecyclerViewWithPlaceholders() {
        for (int i = 0; i < MAX_IMAGES; i++) {
            imageUris.add(Uri.EMPTY); // Uri.EMPTY is used as a placeholder
        }
        imageAdapter.notifyDataSetChanged();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle the error
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.carmarketplaceapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Handle gallery selection
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        addImageUriToList(imageUri);
                    }
                } else if (data.getData() != null) {
                    imageUri = data.getData();
                    addImageUriToList(imageUri);
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Handle camera capture
                imageUri = Uri.fromFile(new File(currentPhotoPath));
                addImageUriToList(imageUri);
            }

            imageAdapter.notifyDataSetChanged();
            Log.d("PostStepOneFragment", "Image URI: " + imageUri.toString());

        }
    }

    private void addImageUriToList(Uri imageUri) {
        // Check if there are placeholders to replace
        int placeholderIndex = imageUris.indexOf(Uri.EMPTY);
        if (placeholderIndex != -1) {
            imageUris.set(placeholderIndex, imageUri);
        } else {
            if (imageUris.size() < MAX_IMAGES) {
                imageUris.add(imageUri);
            }
        }
        imageAdapter.notifyDataSetChanged();
    }


    private void setDropdownAdapter(AutoCompleteTextView autoCompleteTextView, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, data);
        autoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (!TextUtils.isEmpty(autoCompleteTextView.getText().toString())) {
            adapter.getFilter().filter(null);
        }
    }

    @Override
    protected void onPreviousClicked() {
        if (getParentFragment() instanceof PostFragment) {
            ((PostFragment) getParentFragment()).goToPreviousStep();
        }
    }

    @Override
    protected void onNextClicked() {
        if (!validateCurrentStep()) {
            // Navigate to the next step fragment
            PostFragment parentFragment = (PostFragment) getParentFragment();
            if (parentFragment != null) {
                parentFragment.goToNextStep(new PostStepTwoFragment());
            }
        } else {
            // Show error or validation feedback
        }
    }

    @Override
    protected void onCancelClicked() {

    }

    protected boolean validateCurrentStep() {
        // Perform validation logic here and return true if everything is correct

        if (autocompleteCarMake.getText().toString().isEmpty()) {
            autocompleteCarMake.requestFocus();
            return false;
        }

        if (autocompleteCarModel.getText().toString().isEmpty()) {
            autocompleteCarModel.requestFocus();
            return false;
        }

        String yearString = editTextCarYear.getText().toString().trim();
        if (TextUtils.isEmpty(yearString) || !yearString.matches("\\d{4}")) {
            editTextCarYear.requestFocus();
            return false;
        }

        int year;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            editTextCarYear.requestFocus();
            return false;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < 1900 || year > currentYear) {
            editTextCarYear.requestFocus();
            return false;
        }

        if (autocompleteTransmissionType.getText().toString().isEmpty()) {
            autocompleteTransmissionType.requestFocus();
            return false;
        }

        if (autocompleteDrivetrainType.getText().toString().isEmpty()) {
            autocompleteDrivetrainType.requestFocus();
            return false;
        }

        if (autocompleteFuelType.getText().toString().isEmpty()) {
            autocompleteFuelType.requestFocus();
            return false;
        }

        // If all validations pass
        return true;
    }

}
