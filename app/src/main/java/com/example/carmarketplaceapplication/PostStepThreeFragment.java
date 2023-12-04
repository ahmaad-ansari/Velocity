package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class PostStepThreeFragment extends PostStepBaseFragment {
    private SharedViewModel viewModel;
    CarListModel carModel;

    // Inflate the layout and set up necessary data when the fragment is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_step_three, container, false);

        // Obtain the ViewModel to access shared data between fragments
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        carModel = viewModel.getCarListModel().getValue();

        // Customize the 'Next' button text to 'Post'
        Button btnNext = view.findViewById(R.id.button_next);
        btnNext.setText("Post");

        // Fetch the current car model data or create a new one if it's null
        carModel = viewModel.getCarListModel().getValue();
        if (carModel == null) {
            carModel = new CarListModel();
        }

        // Set up the image slider and populate listing details
        ViewPager2 viewPager2 = view.findViewById(R.id.imageSlider);
        ListingViewSetup.initializeImageSliderWithUrls(viewPager2, carModel.getImageUrls(), getContext());
        ListingViewSetup.populateListingDetails(view, carModel, getContext());

        return view;
    }

    // Handle the action when the 'Next' button is clicked
    @Override
    protected void onNextClicked() {
        // Navigate to the next step fragment
        PostFragment parentFragment = (PostFragment) getParentFragment();
        if (parentFragment != null) {
            // Logic to post or update the listing data to Firebase
            FirebaseDataHandler dataHandler = new FirebaseDataHandler();

            if (carModel.getCarId() != null && !carModel.getCarId().isEmpty()) {
                // Update existing listing
                dataHandler.updateCarListing(carModel, new FirebaseDataHandler.UpdateDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("FirebaseDataHandler", "Data updated successfully");
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("FirebaseDataHandler", "Error updating data", exception);
                    }
                });
            } else {
                // Create new listing
                dataHandler.saveCarListing(carModel, new FirebaseDataHandler.SaveDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("FirebaseDataHandler", "Data saved successfully");
                        // Handle successful save
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("FirebaseDataHandler", "Error saving data", exception);
                        // Handle failure
                    }
                });
            }
            // Navigate back to the post fragment
            ((MainActivity) getActivity()).showPostFragment();
        } else {
            // Show error or validation feedback if necessary
        }
    }

    // Validate the current step; return false as this step might not have specific validation
    @Override
    protected boolean validateCurrentStep() {
        return false;
    }
}