package com.example.carmarketplaceapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDataHandler firebaseDataHandler;
    private Map<Marker, CarListModel> markerCarMap = new HashMap<>();
    private View infoWindowView; // View for custom info window


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        infoWindowView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_info_window, null);


        firebaseDataHandler = new FirebaseDataHandler();
        fetchCarLocations();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    private void fetchCarLocations() {
        firebaseDataHandler.fetchCarListings(new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carListings) {
                displayCarLocations(carListings);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle failure to fetch car listings
                Log.e("MapsFragment", "Failed to fetch car listings", exception);
            }
        });
    }

    private void displayCarLocations(List<CarListModel> carListings) {
        List<LatLng> markerPositions = new ArrayList<>();

        for (CarListModel car : carListings) {
            if (car.getOwnerLocation() != null && !car.getOwnerLocation().isEmpty()) {
                LatLng location = getLocationFromAddress(requireContext(), car.getOwnerLocation());
                if (location != null && mMap != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(car.getMake() + " " + car.getModel()));
                    markerPositions.add(location);
                    markerCarMap.put(marker, car);
                }
            }
        }

        if (!markerPositions.isEmpty()) {
            zoomToMarkersBounds(markerPositions);
        }
    }

    private void zoomToMarkersBounds(List<LatLng> markerPositions) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng position : markerPositions) {
            builder.include(position);
        }

        LatLngBounds bounds = builder.build();
        int padding = 100;

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null; // Use default info window background
        }

        @Override
        public View getInfoContents(Marker marker) {
            CarListModel car = markerCarMap.get(marker);
            if (car != null) {
                // Inflate the custom info window layout if it's not already inflated
                if (infoWindowView == null) {
                    infoWindowView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_info_window, null);
                }

                // Bind car details to the custom info window layout
                TextView carYearMakeModel = infoWindowView.findViewById(R.id.text_car_year_make_model);
                carYearMakeModel.setText(car.getYear() + " " + car.getMake() + " " + car.getModel());

                TextView carPrice = infoWindowView.findViewById(R.id.text_car_price);
                carPrice.setText("$" + String.format("%.0f", car.getPrice()));

                TextView carOdometer = infoWindowView.findViewById(R.id.text_car_odometer);
                carOdometer.setText(String.format("%.0f", car.getOdometer()) + " km");

                // Handle clicks for navigating to the car detail fragment
                infoWindowView.setOnClickListener(v -> navigateToCarDetailFragment(car));

                // Set image if available
                ImageView carImage = infoWindowView.findViewById(R.id.image_car);
                List<String> imageUrls = car.getImageUrls();
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    String imageUrl = imageUrls.get(0);
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(carImage);
                }

                return infoWindowView;
            }
            return null;
        }
    }


    // Method to handle navigation to the car detail fragment
    private void navigateToCarDetailFragment(CarListModel car) {
        // Implement navigation logic to the car detail fragment here
        // For example, if you're using Navigation Component:
    }
}
