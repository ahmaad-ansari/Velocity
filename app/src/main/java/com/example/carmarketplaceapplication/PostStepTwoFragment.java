package com.example.carmarketplaceapplication;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostStepTwoFragment extends PostStepBaseFragment implements OnMapReadyCallback  {

    private AutoCompleteTextView autocompleteCarColor, autocompleteNumberOfDoors, autocompleteNumberOfSeats;
    private EditText editTextOdometer, editTextPrice,editTextDescription;
    private CheckBox checkBoxAC, checkBoxNav, checkBoxBT;
    private String[] carColors, numberOfDoors, numberOfSeats;
    private View view;
    private GoogleMap mMap;
    private SharedViewModel viewModel;
    private CarListModel carModel;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Marker currentMarker;

    public PostStepTwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeFormFields(view);
        carModel = viewModel.getCarListModel().getValue();

        if (carModel != null && carModel.getCarId() != null && !carModel.getCarId().isEmpty()) {
            // Load existing data into the fields for editing
            loadCarData(carModel);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_step_two, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button btnNext = view.findViewById(R.id.button_next);
        btnNext.setText("View");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();

            mapFragment.getMapAsync(this);
        }

        return view;
    }

    private void loadCarData(CarListModel carModel) {
        // Load data into AutoCompleteTextViews, EditTexts, and CheckBoxes
        autocompleteCarColor.setText(carModel.getColor(), false);
        editTextOdometer.setText(String.format("%.0f", carModel.getOdometer()));
        editTextPrice.setText(String.format("%.0f", carModel.getPrice()));
        editTextDescription.setText(carModel.getDescription());
        checkBoxAC.setChecked(carModel.isAirConditioning());
        checkBoxNav.setChecked(carModel.isNavigationSystem());
        checkBoxBT.setChecked(carModel.isBluetoothConnectivity());
        autocompleteNumberOfSeats.setText(carModel.getNumberOfSeats(), false);
        autocompleteNumberOfDoors.setText(carModel.getNumberOfDoors(), false);
        //Load map
    }

    private void initializeFormFields(View view) {
        // Initialize AutoCompleteTextViews
        autocompleteCarColor = view.findViewById(R.id.autocomplete_car_color);
        autocompleteNumberOfDoors = view.findViewById(R.id.autocomplete_number_of_doors);
        autocompleteNumberOfSeats = view.findViewById(R.id.autocomplete_number_of_seats);

        editTextOdometer = view.findViewById(R.id.editText_odometer);
        editTextPrice = view.findViewById(R.id.editText_price);
        editTextDescription = view.findViewById(R.id.editText_description);

        checkBoxAC = view.findViewById(R.id.checkbox_air_conditioning);
        checkBoxNav = view.findViewById(R.id.checkbox_navigation_system);
        checkBoxBT = view.findViewById(R.id.checkbox_bluetooth_connectivity);

        carColors = getResources().getStringArray(R.array.car_colors);
        numberOfDoors = getResources().getStringArray(R.array.car_doors);
        numberOfSeats = getResources().getStringArray(R.array.car_seats);

        // Set adapters for the AutoCompleteTextViews
        setDropdownAdapter(autocompleteCarColor, carColors);
        setDropdownAdapter(autocompleteNumberOfDoors, numberOfDoors);
        setDropdownAdapter(autocompleteNumberOfSeats, numberOfSeats);
    }

    private void setDropdownAdapter(AutoCompleteTextView autoCompleteTextView, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, data);
        autoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (!TextUtils.isEmpty(autoCompleteTextView.getText().toString())) {
            adapter.getFilter().filter(null);
        }
    }

    @Override
    protected void onNextClicked() {
        if (validateCurrentStep()) {
            // If all validations pass, navigate to the next step fragment
            PostFragment parentFragment = (PostFragment) getParentFragment();
            if (parentFragment != null) {
                Log.e("TEST", carModel.toString());
                parentFragment.goToNextStep(new PostStepThreeFragment());
            }
        } else {
            // Show error or validation feedback
            // For example, you can show a Toast, Snackbar, or log a message
            Toast.makeText(getContext(), "Please check the input fields.", Toast.LENGTH_SHORT).show();
        }
    }




    protected boolean validateCurrentStep() {
        // Retrieve the current car model from ViewModel or create a new one
        CarListModel carModel = viewModel.getCarListModel().getValue();
        if (carModel == null) {
            carModel = new CarListModel();
        }
        Log.d("DEBUG","Current Car Model: " + carModel);

        // Perform validation logic here and return true if everything is correct

        // Check if car color is empty
        if (autocompleteCarColor.getText().toString().isEmpty()) {
            autocompleteCarColor.requestFocus();
            return false;
        }

        // Check if number of doors is empty
        if (autocompleteNumberOfDoors.getText().toString().isEmpty()) {
            autocompleteNumberOfDoors.requestFocus();
            return false;
        }

        // Check if number of seats is empty
        if (autocompleteNumberOfSeats.getText().toString().isEmpty()) {
            autocompleteNumberOfSeats.requestFocus();
            return false;
        }

        // Validate and handle odometer input
        String odometerString = editTextOdometer.getText().toString().trim();
        if (TextUtils.isEmpty(odometerString) || !odometerString.matches("\\d+")) {
            editTextPrice.requestFocus();
            return false;
        }

        int odometer;
        try {
            odometer = Integer.parseInt(odometerString);
        } catch (NumberFormatException e) {
            editTextOdometer.requestFocus();
            return false;
        }
        if (odometer < 0) {
            editTextOdometer.requestFocus();
            return false;
        }

        // Validate and handle price input
        String priceString = editTextPrice.getText().toString().trim();
        if (TextUtils.isEmpty(priceString) || !priceString.matches("\\d+")) {
            editTextPrice.requestFocus();
            return false;
        }

        int price;
        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            editTextPrice.requestFocus();
            return false;
        }
        if (price < 0) {
            editTextPrice.requestFocus();
            return false;
        }

        // Check if description is empty
        if (editTextDescription.getText().toString().isEmpty()) {
            editTextDescription.requestFocus();
            return false;
        }

        // Update the car model with data from this step
        carModel.setColor(autocompleteCarColor.getText().toString().trim());
        carModel.setOdometer(odometer);
        carModel.setPrice(price);
        carModel.setDescription(editTextDescription.getText().toString().trim());
        carModel.setNumberOfDoors(autocompleteNumberOfDoors.getText().toString().trim());
        carModel.setNumberOfSeats(autocompleteNumberOfSeats.getText().toString().trim());
        carModel.setAirConditioning(checkBoxAC.isChecked());
        carModel.setNavigationSystem(checkBoxNav.isChecked());
        carModel.setBluetoothConnectivity(checkBoxBT.isChecked());

        // Set the default owner location to an empty string
        String newOwnerLocation = "";

        // Retrieve the latest marker if available
        if (currentMarker != null) {
            // Convert the marker's position to an address
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        currentMarker.getPosition().latitude,
                        currentMarker.getPosition().longitude,
                        1
                );
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    newOwnerLocation = address.getAddressLine(0); // Get the address line
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Update the car model's ownerLocation with the new address
        carModel.setOwnerLocation(newOwnerLocation);

        // Update the ViewModel
        viewModel.setCarListModel(carModel);

        // If all validations pass
        return true;
    }

    // Get a handle to the GoogleMap object and display marker
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the user's location on the map
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Enable the user's location and move the map camera to the current location
        mMap.setMyLocationEnabled(true);

        // Assuming carModel is initialized and contains the ownerLocation
        String ownerLocation = carModel.getOwnerLocation();

        if (ownerLocation != null && !ownerLocation.isEmpty()) {
            // Use Geocoding to convert the address (ownerLocation) to LatLng
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(ownerLocation, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                    // Add a marker at the retrieved location
                    MarkerOptions markerOptions = new MarkerOptions().position(location).draggable(true);
                    currentMarker = mMap.addMarker(markerOptions);

                    // Move camera to the retrieved location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If ownerLocation is null, set the map to the user's current location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                // Add a marker at the user's current location
                                MarkerOptions markerOptions = new MarkerOptions().position(currentLocation).draggable(true);
                                currentMarker = mMap.addMarker(markerOptions);

                                // Move camera to the user's current location
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                            }
                        }
                    });
        }

        // Set a marker drag listener to update the marker's position when dragged by the user
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // Called when the user starts dragging the marker
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // Called repeatedly as the user drags the marker
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Called when the user finishes dragging the marker
                currentMarker = marker; // Update the current marker

                // Convert the dragged marker's position to an address and update carModel's ownerLocation
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String newOwnerLocation = address.getAddressLine(0); // Get the address line
                        carModel.setOwnerLocation(newOwnerLocation);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if the permission request code matches the location permission request code
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the map if available
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
