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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_step_three, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        carModel = viewModel.getCarListModel().getValue();


        Button btnNext = view.findViewById(R.id.button_next);
        btnNext.setText("Post");

        carModel = viewModel.getCarListModel().getValue();
        if (carModel == null) {
            carModel = new CarListModel();
        }

        // Inside onCreateView or another suitable method
        ViewPager2 viewPager2 = view.findViewById(R.id.imageSlider);
        ListingViewSetup.initializeImageSliderWithUrls(viewPager2, carModel.getImageUrls(), getContext());

        ListingViewSetup.populateListingDetails(view, carModel, getContext());


        return view;
    }

    @Override
    protected void onNextClicked() {
        // Navigate to the next step fragment
        PostFragment parentFragment = (PostFragment) getParentFragment();
        if (parentFragment != null) {
            // Logic to post listing
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
            ((MainActivity) getActivity()).showPostFragment();
        } else {
            // Show error or validation feedback
        }
    }

    @Override
    protected boolean validateCurrentStep() {
        return false;
    }
}