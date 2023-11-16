package com.example.carmarketplaceapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostFragment extends Fragment {

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        if (savedInstanceState == null) {
            displayInitialStepFragment();
        }

        return view;
    }

    private void displayInitialStepFragment() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager != null) {
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.post_fragment_container, new PostStepOneFragment());
            fragmentTransaction.commit();
        }
    }

    public void goToPreviousStep() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager != null && childFragmentManager.getBackStackEntryCount() > 0) {
            childFragmentManager.popBackStack();
        } else {
            // Optionally handle the case when there is no previous step
            // e.g., close the fragment or navigate to another screen
        }
    }

    public void goToNextStep(Fragment nextStepFragment) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager != null) {
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(R.id.post_fragment_container, nextStepFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
