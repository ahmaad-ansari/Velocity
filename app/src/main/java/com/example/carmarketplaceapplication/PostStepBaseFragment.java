package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;

public abstract class PostStepBaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupNavigationButtons(view);
    }

    protected void setupNavigationButtons(View view) {
        // Previous Button
        Button buttonPrevious = view.findViewById(R.id.button_previous);
        if (buttonPrevious != null) {
            buttonPrevious.setOnClickListener(v -> onPreviousClicked());
        }

        // Next Button
        Button buttonNext = view.findViewById(R.id.button_next);
        if (buttonNext != null) {
            buttonNext.setOnClickListener(v -> onNextClicked());
        }

        // Cancel Button
        Button buttonCancel = view.findViewById(R.id.button_cancel);
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> onCancelClicked());
        }
    }

    protected void onPreviousClicked() {
        if (getParentFragment() instanceof PostFragment) {
            ((PostFragment) getParentFragment()).goToPreviousStep();
        }
    }

    protected void onCancelClicked() {
        ((MainActivity) getActivity()).showPostFragment();
    }

    protected abstract void onNextClicked();

    protected abstract boolean validateCurrentStep();
}
