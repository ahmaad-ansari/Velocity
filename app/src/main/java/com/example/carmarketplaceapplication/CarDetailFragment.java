package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.SupportMapFragment;

public class CarDetailFragment extends Fragment {

    // Constant key for passing CarListModel through arguments
    private static final String ARG_CAR_MODEL = "carModel";

    // Instance variable to store CarListModel data
    private CarListModel carModel;

    // Factory method to create a new instance of CarDetailFragment with arguments
    public static CarDetailFragment newInstance(CarListModel carModel) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CAR_MODEL, carModel);
        fragment.setArguments(args);
        return fragment;
    }

    // Called to create the view hierarchy associated with the fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);

        // Retrieve CarListModel from arguments
        if (getArguments() != null) {
            carModel = (CarListModel) getArguments().getSerializable(ARG_CAR_MODEL);
        }

        // Initialize and setup the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        // Asynchronously get the GoogleMap and set it up for the car location
        mapFragment.getMapAsync(googleMap -> {
            if (carModel != null) {
                ListingViewSetup.setupMapForCarLocation(googleMap, requireContext(), carModel);
            }
        });

        // Retrieve CarListModel again (redundant assignment)
        CarListModel carModel = null;
        if (getArguments() != null) {
            carModel = (CarListModel) getArguments().getSerializable(ARG_CAR_MODEL);
        }

        // Initialize image slider and populate listing details if CarListModel is available
        if (carModel != null) {
            ViewPager2 viewPager2 = view.findViewById(R.id.imageSlider);
            ListingViewSetup.initializeImageSliderWithUrls(viewPager2, carModel.getImageUrls(), getContext());

            ListingViewSetup.populateListingDetails(view, carModel, getContext());
        }

        return view;
    }

    // Called when the fragment is being destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the SupportMapFragment to prevent memory leaks
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            requireActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
    }

    // Called immediately after onViewCreated(View, Bundle)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize and setup the SupportMapFragment again (possibly redundant)
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        // Asynchronously get the GoogleMap and set it up for the car location
        mapFragment.getMapAsync(googleMap -> {
            ListingViewSetup.setupMapForCarLocation(googleMap, requireContext(), carModel);
        });

        // Handle button click to initiate SMS to the phone number displayed
        Button btnContact = view.findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView phoneNumberTextView = view.findViewById(R.id.tvPhoneNumber);
                String phoneNumberWithFormatting = phoneNumberTextView.getText().toString();
                String phoneNumber = phoneNumberWithFormatting.replaceAll("[^\\d]", "");

                // Start SMS intent with the extracted phone number
                if (!TextUtils.isEmpty(phoneNumber)) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setData(Uri.parse("sms:" + phoneNumber));
                    startActivity(smsIntent);
                } else {
                    // Handle empty phone number
                }
            }
        });
    }
}
