package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class PostStepThreeFragment extends PostStepBaseFragment implements OnMapReadyCallback {

    LinearLayout llHighlightsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_step_three, container, false);

        Button btnNext = view.findViewById(R.id.button_next);
        btnNext.setText("Post");

        // Get a handle to the fragment and register the callback.
        // Use getChildFragmentManager() or getFragmentManager() based on your fragment setup
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        initializeHighlights(view);

        return view;
    }

    private void initializeHighlights(View view) {
        llHighlightsContainer = view.findViewById(R.id.llHighlights);

        int[] icons = {R.drawable.ic_mileage, R.drawable.ic_transmission, R.drawable.ic_drivetrain, R.drawable.ic_fuel, R.drawable.ic_seat, R.drawable.ic_door, R.drawable.ic_ac, R.drawable.ic_nav, R.drawable.ic_bt};
        String[] headings = {"Mileage", "Transmission", "Drivetrain", "Fuel Type", "Seats", "Doors", "Air Conditioning", "Navigation System", "Bluetooth Connectivity"};
        String[] details = {"30,000 km","Automatic", "4WD", "Gas", "4", "4", "Yes", "No", "Yes"};

        for(int i = 0; i < icons.length; i++) {
            View item = createVehicleDetailItem(view, icons[i], headings[i], details[i]);
            llHighlightsContainer.addView(item);
        }
    }

    private View createVehicleDetailItem(View parentView, int iconResId, String heading, String detail) {
        View itemView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_vehicle_detail, llHighlightsContainer, false);

        ImageView imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        TextView textViewHeading = itemView.findViewById(R.id.textViewHeading);
        TextView textViewDetail = itemView.findViewById(R.id.textViewDetail);

        imageViewIcon.setImageResource(iconResId);
        textViewHeading.setText(heading);
        textViewDetail.setText(detail);

        return itemView;
    }

    @Override
    protected void onNextClicked() {
        if (!validateCurrentStep()) {
            // Navigate to the next step fragment
            PostFragment parentFragment = (PostFragment) getParentFragment();
            if (parentFragment != null) {
//                parentFragment.goToNextStep(new PostStepThreeFragment());
            }
        } else {
            // Show error or validation feedback
        }
    }

    @Override
    protected boolean validateCurrentStep() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.
                newLatLngZoom(new LatLng(43.874320, -79.007450), 10));

    }
}