package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class PostStepOneFragment extends PostStepBaseFragment  {

    private AutoCompleteTextView autocompleteCarMake, autocompleteCarModel, autocompleteTransmissionType, autocompleteDrivetrainType, autocompleteFuelType;
    private EditText editTextCarYear;
    private String[] carMakes, carModels, transmissionTypes, drivetrainTypes, fuelTypes;
    private View view;

    public PostStepOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeFormFields(view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_step_one, container, false);

        return view;
    }

    private void initializeFormFields(View view) {
        // Initialize AutoCompleteTextViews
        autocompleteCarMake = view.findViewById(R.id.autocomplete_car_make);
        autocompleteCarModel = view.findViewById(R.id.autocomplete_car_model);
        editTextCarYear = view.findViewById(R.id.editText_car_year);
        autocompleteTransmissionType = view.findViewById(R.id.autocomplete_transmission_type);
        autocompleteDrivetrainType = view.findViewById(R.id.autocomplete_drivetrain);
        autocompleteFuelType = view.findViewById(R.id.autocomplete_fuel_type);

        carMakes = getResources().getStringArray(R.array.car_makes);
        carModels = getResources().getStringArray(R.array.car_models);
        transmissionTypes = getResources().getStringArray(R.array.transmission_types);
        drivetrainTypes = getResources().getStringArray(R.array.drivetrain_types);
        fuelTypes = getResources().getStringArray(R.array.fuel_types);

        // Set adapters for the AutoCompleteTextViews
        setDropdownAdapter(autocompleteCarMake, carMakes);
        setDropdownAdapter(autocompleteCarModel, carModels);
        setDropdownAdapter(autocompleteTransmissionType, transmissionTypes);
        setDropdownAdapter(autocompleteDrivetrainType, drivetrainTypes);
        setDropdownAdapter(autocompleteFuelType, fuelTypes);
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
    protected void onPreviousClicked() {
        if (getParentFragment() instanceof PostFragment) {
            ((PostFragment) getParentFragment()).goToPreviousStep();
        }
    }

    @Override
    protected void onNextClicked() {
        if (!validateCurrentStep()) {
            // Navigate to the next step fragment
            PostFragment parentFragment = (PostFragment) getParentFragment();
            if (parentFragment != null) {
                parentFragment.goToNextStep(new PostStepTwoFragment());
            }
        } else {
            // Show error or validation feedback
        }
    }

    @Override
    protected void onCancelClicked() {

    }

    protected boolean validateCurrentStep() {
        // Perform validation logic here and return true if everything is correct

        if (autocompleteCarMake.getText().toString().isEmpty()) {
            autocompleteCarMake.requestFocus();
            return false;
        }

        if (autocompleteCarModel.getText().toString().isEmpty()) {
            autocompleteCarModel.requestFocus();
            return false;
        }

        String yearString = editTextCarYear.getText().toString().trim();
        if (TextUtils.isEmpty(yearString) || !yearString.matches("\\d{4}")) {
            editTextCarYear.requestFocus();
            return false;
        }

        int year;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            editTextCarYear.requestFocus();
            return false;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < 1900 || year > currentYear) {
            editTextCarYear.requestFocus();
            return false;
        }

        if (autocompleteTransmissionType.getText().toString().isEmpty()) {
            autocompleteTransmissionType.requestFocus();
            return false;
        }

        if (autocompleteDrivetrainType.getText().toString().isEmpty()) {
            autocompleteDrivetrainType.requestFocus();
            return false;
        }

        if (autocompleteFuelType.getText().toString().isEmpty()) {
            autocompleteFuelType.requestFocus();
            return false;
        }

        // If all validations pass
        return true;
    }

}
