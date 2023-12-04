package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    // Listener to communicate filter changes to the parent fragment/activity
    private FilterListener filterListener;

    // Checkboxes and buttons for filter options
    private CheckBox checkboxAirConditioning;
    private CheckBox checkboxNavigation;
    private CheckBox checkboxBluetooth;
    private Button buttonApply;

    // AutoCompleteTextViews for filtering by car attributes
    private AutoCompleteTextView autoCompleteMake;
    private AutoCompleteTextView autoCompleteModel;
    private AutoCompleteTextView autoCompleteTransmission;
    private AutoCompleteTextView autoCompleteDrivetrain;
    private AutoCompleteTextView autoCompleteFuel;

    // Empty public constructor
    public FilterBottomSheetFragment() {}

    // Static method to create a new instance of the fragment and pass the listener
    public static FilterBottomSheetFragment newInstance(FilterListener listener) {
        FilterBottomSheetFragment fragment = new FilterBottomSheetFragment();
        fragment.filterListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing views and setting up listeners for AutoCompleteTextViews and button click
        checkboxAirConditioning = view.findViewById(R.id.checkbox_air_conditioning);
        checkboxNavigation = view.findViewById(R.id.checkbox_navigation_system);
        checkboxBluetooth = view.findViewById(R.id.checkbox_bluetooth);
        buttonApply = view.findViewById(R.id.button_apply_filters);

        autoCompleteMake = view.findViewById(R.id.autocomplete_car_make);
        autoCompleteModel = view.findViewById(R.id.autocomplete_car_model);
        autoCompleteTransmission = view.findViewById(R.id.autocomplete_transmission_type);
        autoCompleteDrivetrain = view.findViewById(R.id.autocomplete_drivetrain_type);
        autoCompleteFuel = view.findViewById(R.id.autocomplete_fuel_type);

        // Setting up AutoCompleteTextViews with arrays and item selection listeners
        setupAutoCompleteTextView(autoCompleteMake, R.array.car_makes);
        setupAutoCompleteTextView(autoCompleteTransmission, R.array.transmission_types);
        setupAutoCompleteTextView(autoCompleteDrivetrain, R.array.drivetrain_types);
        setupAutoCompleteTextView(autoCompleteFuel, R.array.fuel_types);

        autoCompleteMake.setOnItemClickListener((parent, view1, position, id) -> {
            // When a car make is selected, populate models for that make
            String selectedMake = (String) parent.getItemAtPosition(position);
            int modelsArrayResourceId = getResources().getIdentifier(selectedMake.toLowerCase() + "_models", "array", requireContext().getPackageName());
            String[] modelsArray = getResources().getStringArray(modelsArrayResourceId);
            ArrayAdapter<String> modelsAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    modelsArray
            );
            autoCompleteModel.setAdapter(modelsAdapter);
        });

        buttonApply.setOnClickListener(v -> applyFilters()); // Applying selected filters on button click
    }

    // Method to set up an AutoCompleteTextView with a specified array
    private void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView, int itemsArrayId) {
        String[] itemsArray = getResources().getStringArray(itemsArrayId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                itemsArray
        );
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Actions to perform when an item is selected in the AutoCompleteTextView
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Perform actions when an item is selected
        });
    }

    // Method to apply selected filters and notify the listener
    private void applyFilters() {
        FilterParams filterParams = new FilterParams();
        filterParams.airConditioning = checkboxAirConditioning.isChecked();
        filterParams.navigation = checkboxNavigation.isChecked();
        filterParams.bluetooth = checkboxBluetooth.isChecked();
        filterParams.make = autoCompleteMake.getText().toString();
        filterParams.model = autoCompleteModel.getText().toString();
        filterParams.transmission = autoCompleteTransmission.getText().toString();
        filterParams.drivetrain = autoCompleteDrivetrain.getText().toString();
        filterParams.fuel = autoCompleteFuel.getText().toString();

        // Notify listener with applied filters and dismiss the bottom sheet
        if (filterListener != null) {
            filterListener.onFiltersApplied(filterParams);
        }
        dismiss(); // Close the bottom sheet after applying filters
    }

    // Interface to communicate applied filters to the parent fragment/activity
    public interface FilterListener {
        void onFiltersApplied(FilterParams filterParams);
    }

    // Class to hold filter parameters
    public static class FilterParams {
        boolean airConditioning;
        boolean bluetooth;
        boolean navigation;
        String make;
        String model;
        String transmission;
        String drivetrain;
        String fuel;

        // Getters and setters for filter parameters
        public boolean isNavigation() {
            return navigation;
        }

        public void setNavigation(boolean navigation) {
            this.navigation = navigation;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        // Additional getters and setters for other filter parameters
    }
}