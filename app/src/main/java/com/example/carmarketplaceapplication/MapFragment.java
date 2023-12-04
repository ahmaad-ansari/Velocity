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

import com.example.carmarketplaceapplication.CarListModel;
import com.example.carmarketplaceapplication.FirebaseDataHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDataHandler firebaseDataHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        firebaseDataHandler = new FirebaseDataHandler();
        fetchCarLocations();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
                    mMap.addMarker(new MarkerOptions().position(location).title(car.getMake() + " " + car.getModel()));
                    markerPositions.add(location);
                }
            }
        }

        if (!markerPositions.isEmpty()) {
            zoomToMarkersBounds(markerPositions);
        }
    }

    // Method to calculate bounding box and zoom to markers' bounds
    private void zoomToMarkersBounds(List<LatLng> markerPositions) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng position : markerPositions) {
            builder.include(position);
        }

        LatLngBounds bounds = builder.build();
        int padding = 100; // Adjust padding as needed

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    // Method to convert address to LatLng
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
}









