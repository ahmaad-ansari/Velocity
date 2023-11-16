package com.example.carmarketplaceapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostStepThreeFragment extends PostStepBaseFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int MAX_IMAGES = 8;
    private String currentPhotoPath;


    private GridView gridView;
    private Button btnAddImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_step_three, container, false);

        gridView = view.findViewById(R.id.gridView);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(v -> showImagePickOptions());

        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageUris);
        gridView.setAdapter(imageAdapter);

        btnAddImage.setOnClickListener(v -> {
            // Add logic to show image picking options
            showImagePickOptions();
        });

        return view;
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


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
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
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (imageUris.size() < MAX_IMAGES) {
                            imageUris.add(imageUri);
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    if (imageUris.size() < MAX_IMAGES) {
                        imageUris.add(imageUri);
                    }
                }
                imageAdapter.notifyDataSetChanged();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Handle captured image
            }
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
                parentFragment.goToNextStep(new PostStepFourFragment());
            }
        } else {
            // Show error or validation feedback
        }
    }

    @Override
    protected void onCancelClicked() {

    }

    @Override
    protected boolean validateCurrentStep() {
        return false;
    }

    // Add other required methods like permission checks
}
