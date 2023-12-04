package com.example.carmarketplaceapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    // Called when the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    // Fetches car locations from Firebase and displays markers on the map
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

    // Displays markers for car locations on the map
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

    // Zooms the map camera to fit all markers within the bounds
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

    // Retrieves LatLng from an address string using Geocoder
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

    // Custom Info Window Adapter for displaying car details in markers' info windows
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
                // ... (Code to bind car details to the custom info window layout)

                return infoWindowView;
            }
            return null;
        }
    }

    // Method to handle navigation to the car detail fragment
    private void navigateToCarDetailFragment(CarListModel car) {
        // Implement navigation logic to the car detail fragment here
    }
}
