package com.example.carmarketplaceapplication;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputLayout;
import java.util.function.Predicate;

public class GenericTextWatcher implements TextWatcher {
    private final TextInputLayout inputLayout;
    private final Predicate<String> validationCondition;
    private final String errorMessage;

    public GenericTextWatcher(TextInputLayout inputLayout, Predicate<String> validationCondition, String errorMessage) {
        this.inputLayout = inputLayout;
        this.validationCondition = validationCondition;
        this.errorMessage = errorMessage;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No action needed here
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (validationCondition.test(s.toString())) {
            inputLayout.setError(null); // Clear the error
            inputLayout.setErrorEnabled(false); // Remove extra space taken by the error message
        } else {
            inputLayout.setError(errorMessage);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // No action needed here
    }
}
