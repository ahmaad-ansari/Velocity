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

    private static final String ARG_CAR_MODEL = "carModel";
    private CarListModel carModel;


    public static CarDetailFragment newInstance(CarListModel carModel) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CAR_MODEL, carModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);

        // Retrieve carModel from arguments
        if (getArguments() != null) {
            carModel = (CarListModel) getArguments().getSerializable(ARG_CAR_MODEL);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(googleMap -> {
            if (carModel != null) {
                ListingViewSetup.setupMapForCarLocation(googleMap, requireContext(), carModel);
            }
        });

        CarListModel carModel = null;
        if (getArguments() != null) {
            carModel = (CarListModel) getArguments().getSerializable(ARG_CAR_MODEL);
        }

        if (carModel != null) {
            ViewPager2 viewPager2 = view.findViewById(R.id.imageSlider);
            ListingViewSetup.initializeImageSliderWithUrls(viewPager2, carModel.getImageUrls(), getContext());

            ListingViewSetup.populateListingDetails(view, carModel, getContext());
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            requireActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(googleMap -> {
            // Setup the map based on the carModel
            ListingViewSetup.setupMapForCarLocation(googleMap, requireContext(), carModel);
        });

        Button btnContact = view.findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView phoneNumberTextView = view.findViewById(R.id.tvPhoneNumber);
                String phoneNumberWithFormatting = phoneNumberTextView.getText().toString();
                String phoneNumber = phoneNumberWithFormatting.replaceAll("[^\\d]", "");

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
