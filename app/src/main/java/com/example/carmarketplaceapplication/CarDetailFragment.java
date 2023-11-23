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
            ViewPager2 viewPager2 = view.findViewById(R.id.imageSlider);
            ListingViewSetup.initializeImageSliderWithUrls(viewPager2, carModel.getImageUrls(), getContext());

            ListingViewSetup.populateListingDetails(view, carModel, getContext());

        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnContact = view.findViewById(R.id.btnContact); // Replace with your actual button ID
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView phoneNumberTextView = view.findViewById(R.id.tvPhoneNumber); // Replace with your actual TextView ID
                String phoneNumberWithFormatting = phoneNumberTextView.getText().toString();

                // Remove non-numeric characters
                String phoneNumber = phoneNumberWithFormatting.replaceAll("[^\\d]", "");

                if (!TextUtils.isEmpty(phoneNumber)) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setData(Uri.parse("sms:" + phoneNumber));
                    startActivity(smsIntent);
                } else {
                    // Handle the case where the phone number is empty or contains no digits
                    // You might want to show a Toast or perform other actions
                }
            }
        });
    }
}
