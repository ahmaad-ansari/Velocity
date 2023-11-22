package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class CarDetailFragment extends Fragment {

    private static final String ARG_CAR_MODEL = "carModel";

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

        CarListModel carModel = null;
        if (getArguments() != null) {
            carModel = (CarListModel) getArguments().getSerializable(ARG_CAR_MODEL);
        }

        if (carModel != null) {
            // Set up views to display car details
//            TextView textViewCarDetails = view.findViewById(R.id.textViewCarDetails);
//            textViewCarDetails.setText(carModel.toString()); // Simplified example
            ViewPager2 viewPager2 = view.findViewById(R.id.imageSlider);
            ListingViewSetup.initializeImageSliderWithUrls(viewPager2, carModel.getImageUrls(), getContext());

            ListingViewSetup.populateListingDetails(view, carModel, getContext());
        }

        return view;
    }
}
