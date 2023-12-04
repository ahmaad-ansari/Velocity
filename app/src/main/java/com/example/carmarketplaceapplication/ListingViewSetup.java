package com.example.carmarketplaceapplication;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListingViewSetup {

    static LinearLayout llHighlightsContainer;
    static TextView tvYearMakeModel, tvPrice, tvDescription, tvOwnerName, tvOwnerPhoneNumber;

    public static void initializeImageSliderWithUrls(ViewPager2 viewPager2, List<String> imageUrls, Context context) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            String placeholderUrl = "android.resource://" + context.getPackageName() + "/" + R.drawable.placeholder_image;
            imageUrls = Collections.singletonList(placeholderUrl);
        }

        ImageSliderAdapter adapter = new ImageSliderAdapter(context, imageUrls);
        viewPager2.setAdapter(adapter);
    }

    public static void populateListingDetails(View view, CarListModel carModel, Context context) {
        llHighlightsContainer = view.findViewById(R.id.llHighlights);
        tvYearMakeModel = view.findViewById(R.id.tvCarMakeModel);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvOwnerName = view.findViewById(R.id.tvName);
        tvOwnerPhoneNumber = view.findViewById(R.id.tvPhoneNumber);

        tvYearMakeModel.setText(carModel.getYear() + " " + carModel.getMake() + " " + carModel.getModel());
        tvPrice.setText(String.format("$%,.2f", carModel.getPrice()));
        tvDescription.setText(carModel.getDescription());
        tvOwnerName.setText(carModel.getOwnerName());
        tvOwnerPhoneNumber.setText(carModel.getOwnerContactNumber());

        Resources res = context.getResources();

        int[] icons = {
                R.drawable.ic_mileage,
                R.drawable.ic_transmission,
                R.drawable.ic_drivetrain,
                R.drawable.ic_fuel,
                R.drawable.ic_color,
                R.drawable.ic_seat,
                R.drawable.ic_door,
                R.drawable.ic_ac,
                R.drawable.ic_nav,
                R.drawable.ic_bt
        };

        String[] headings = res.getStringArray(R.array.headings_array);
        String[] details = {
                String.format("%,.0f", carModel.getOdometer()),
                carModel.getTransmissionType(),
                carModel.getDrivetrainType(),
                carModel.getFuelType(),
                carModel.getColor(),
                String.valueOf(carModel.getNumberOfSeats()),
                String.valueOf(carModel.getNumberOfDoors()),
                carModel.isAirConditioning() ? "Yes" : "No",
                carModel.isNavigationSystem() ? "Yes" : "No",
                carModel.isBluetoothConnectivity() ? "Yes" : "No"
        };

        for (int i = 0; i < icons.length; i++) {
            View item = createVehicleDetailItem(llHighlightsContainer, icons[i], headings[i], details[i], context);
            llHighlightsContainer.addView(item);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GoogleMap mMap = googleMap;
                setupMapForCarLocation(mMap, context, carModel);
            }
        });
    }

    public static void setupMapForCarLocation(GoogleMap mMap, Context context, CarListModel carModel) {
        String ownerLocation = carModel.getOwnerLocation();
        if (ownerLocation != null && !ownerLocation.isEmpty()) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(ownerLocation, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(location).title(carModel.getYear() + " " + carModel.getMake() + " " + carModel.getModel()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static View createVehicleDetailItem(ViewGroup parent, int iconResId, String heading, String detail, Context context) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_vehicle_detail, parent, false);

        ImageView imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        TextView textViewHeading = itemView.findViewById(R.id.textViewHeading);
        TextView textViewDetail = itemView.findViewById(R.id.textViewDetail);

        imageViewIcon.setImageResource(iconResId);
        textViewHeading.setText(heading);
        textViewDetail.setText(detail);

        return itemView;
    }
}
