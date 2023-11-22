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

    private FilterListener filterListener;
    private CheckBox checkboxAirConditioning;
    private CheckBox checkboxNavigation;
    private CheckBox checkboxBluetooth;
    private Button buttonApply;
    private AutoCompleteTextView autoCompleteMake;
    private AutoCompleteTextView autoCompleteModel;
    private AutoCompleteTextView autoCompleteTransmission;
    private AutoCompleteTextView autoCompleteDrivetrain;
    private AutoCompleteTextView autoCompleteFuel;

    public FilterBottomSheetFragment() {
        // Required empty public constructor
    }

    // Static method to create an instance of the fragment and pass the listener
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

        checkboxAirConditioning = view.findViewById(R.id.checkbox_air_conditioning);
        checkboxNavigation = view.findViewById(R.id.checkbox_navigation_system);
        checkboxBluetooth = view.findViewById(R.id.checkbox_bluetooth);
        buttonApply = view.findViewById(R.id.button_apply_filters);

        autoCompleteMake = view.findViewById(R.id.autocomplete_car_make);
        autoCompleteModel = view.findViewById(R.id.autocomplete_car_model);
        autoCompleteTransmission = view.findViewById(R.id.autocomplete_transmission_type);
        autoCompleteDrivetrain = view.findViewById(R.id.autocomplete_drivetrain_type);
        autoCompleteFuel = view.findViewById(R.id.autocomplete_fuel_type);

        setupAutoCompleteTextView(autoCompleteMake, R.array.car_makes);
//        setupAutoCompleteTextView(autoCompleteModel, R.array.default_empty_array); // Replace with the appropriate array
        setupAutoCompleteTextView(autoCompleteTransmission, R.array.transmission_types);
        setupAutoCompleteTextView(autoCompleteDrivetrain, R.array.drivetrain_types);
        setupAutoCompleteTextView(autoCompleteFuel, R.array.fuel_types);

        autoCompleteMake.setOnItemClickListener((parent, view1, position, id) -> {
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

        buttonApply.setOnClickListener(v -> applyFilters());
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView, int itemsArrayId) {
        String[] itemsArray = getResources().getStringArray(itemsArrayId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                itemsArray
        );
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Perform actions when an item is selected
        });
    }

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

        if (filterListener != null) {
            filterListener.onFiltersApplied(filterParams);
        }

        dismiss(); // Close the bottom sheet after applying filters
    }

    public interface FilterListener {
        void onFiltersApplied(FilterParams filterParams);
    }

    public static class FilterParams {
        boolean airConditioning;
        boolean bluetooth;
        boolean navigation;
        String make;
        String model;
        String transmission;
        String drivetrain;
        String fuel;

        public boolean isAirConditioning() {
            return airConditioning;
        }

        public void setAirConditioning(boolean airConditioning) {
            this.airConditioning = airConditioning;
        }

        public boolean isNavigation() {
            return navigation;
        }

        public void setNavigation(boolean navigation) {
            this.navigation = navigation;
        }

        public boolean isBluetooth() {
            return bluetooth;
        }

        public void setBluetooth(boolean bluetooth) {
            this.bluetooth = bluetooth;
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

        public String getTransmission() {
            return transmission;
        }

        public void setTransmission(String transmission) {
            this.transmission = transmission;
        }

        public String getDrivetrain() {
            return drivetrain;
        }

        public void setDrivetrain(String drivetrain) {
            this.drivetrain = drivetrain;
        }

        public String getFuel() {
            return fuel;
        }

        public void setFuel(String fuel) {
            this.fuel = fuel;
        }
    }
}


