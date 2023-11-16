package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class PostStepTwoFragment extends PostStepBaseFragment  {

    private AutoCompleteTextView autocompleteCarColor, autocompleteNumberOfDoors, autocompleteNumberOfSeats;
    private EditText editTextOdometer, editTextPrice,editTextDescription;
    private CheckBox checkBoxAC, checkBoxNav, checkBoxBT, checkBoxSunroof;
    private String[] carColors, numberOfDoors, numberOfSeats;
    private View view;

    public PostStepTwoFragment() {
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
        view = inflater.inflate(R.layout.fragment_post_step_two, container, false);

        return view;
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
        checkBoxSunroof = view.findViewById(R.id.checkbox_sunroof);

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
                parentFragment.goToNextStep(new PostStepThreeFragment());
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

        if (autocompleteCarColor.getText().toString().isEmpty()) {
            autocompleteCarColor.requestFocus();
            return false;
        }

        if (autocompleteNumberOfDoors.getText().toString().isEmpty()) {
            autocompleteNumberOfDoors.requestFocus();
            return false;
        }

        if (autocompleteNumberOfSeats.getText().toString().isEmpty()) {
            autocompleteNumberOfSeats.requestFocus();
            return false;
        }

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

        if (editTextDescription.getText().toString().isEmpty()) {
            editTextDescription.requestFocus();
            return false;
        }

        // If all validations pass
        return true;
    }
}
