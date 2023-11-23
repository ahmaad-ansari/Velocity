package com.example.carmarketplaceapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private RecyclerView regularCarsRecyclerView;
    private RegularCarsAdapter regularCarsAdapter;




    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        if (savedInstanceState == null) {
            displayInitialFragment(view);
        }

        return view;
    }

    private void displayInitialFragment(View view) {
        // Here, you'll populate the list of posts in the fragment
        // Create a RecyclerView or ListView and populate it with user's posts
        regularCarsRecyclerView = view.findViewById(R.id.regularListingsRecyclerView);

        setupRegularCarsRecyclerView();
        loadCarListings();


        Button btnPost = view.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFirstStep();
            }
        });
    }

    private void setupRegularCarsRecyclerView() {
        regularCarsAdapter = new RegularCarsAdapter(new RegularCarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarListModel item) {
                navigateToCarEditFragment(item);
            }
        });
        regularCarsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        regularCarsRecyclerView.setAdapter(regularCarsAdapter);
    }

    private void navigateToCarEditFragment(CarListModel carModel) {
        // Update the SharedViewModel with the selected CarListModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.setCarListModel(carModel);

        // Navigate to the PostStepOneFragment for editing
        PostStepOneFragment postStepOneFragment = new PostStepOneFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, postStepOneFragment) // Replace with the appropriate container ID
                .addToBackStack(null) // Add this transaction to the back stack
                .commit();
    }


    private void loadCarListings() {
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();
        dataHandler.fetchUserCarListings(new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carList) {
                // Update your RecyclerViews here
                regularCarsAdapter.setCarListings(carList); // Same as above
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle the error
                Log.e("HomeFragment", "Error fetching car listings", exception);
            }
        });
    }




    private void goToFirstStep() {
        // Replace the displayed list of posts with the first step fragment
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
