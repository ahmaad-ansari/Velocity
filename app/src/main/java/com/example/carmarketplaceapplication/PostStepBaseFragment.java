package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;

// This abstract class serves as a base for fragments within the post creation process
public abstract class PostStepBaseFragment extends Fragment {

    // This method is called when the view has been created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up navigation buttons
        setupNavigationButtons(view);
    }

    // Method to set up navigation buttons (Previous, Next, Cancel)
    protected void setupNavigationButtons(View view) {
        // Previous Button
        Button buttonPrevious = view.findViewById(R.id.button_previous);
        if (buttonPrevious != null) {
            // Set an onClickListener for the previous button
            buttonPrevious.setOnClickListener(v -> onPreviousClicked());
        }

        // Next Button
        Button buttonNext = view.findViewById(R.id.button_next);
        if (buttonNext != null) {
            // Set an onClickListener for the next button
            buttonNext.setOnClickListener(v -> onNextClicked());
        }

        // Cancel Button
        Button buttonCancel = view.findViewById(R.id.button_cancel);
        if (buttonCancel != null) {
            // Set an onClickListener for the cancel button
            buttonCancel.setOnClickListener(v -> onCancelClicked());
        }
    }

    // Method invoked when the "Previous" button is clicked
    protected void onPreviousClicked() {
        // If the parent fragment is the PostFragment, call its goToPreviousStep() method
        if (getParentFragment() instanceof PostFragment) {
            ((PostFragment) getParentFragment()).goToPreviousStep();
        }
    }

    // Method invoked when the "Cancel" button is clicked
    protected void onCancelClicked() {
        // If the parent activity is MainActivity, show the PostFragment
        ((MainActivity) getActivity()).showPostFragment();
    }

    // Abstract method for handling the "Next" button click, to be implemented in child classes
    protected abstract void onNextClicked();

    // Abstract method for validating the current step, to be implemented in child classes
    protected abstract boolean validateCurrentStep();
}
