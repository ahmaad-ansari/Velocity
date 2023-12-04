package com.example.carmarketplaceapplication;

import android.app.AlertDialog;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private RecyclerView regularCarsRecyclerView; // RecyclerView to display user's posts
    private RegularCarsAdapter regularCarsAdapter; // Adapter for the RecyclerView

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Check if the fragment is created for the first time (not due to configuration changes)
        if (savedInstanceState == null) {
            // Display the initial fragment view with user's posts
            displayInitialFragment(view);
        }

        return view; // Return the prepared view for the fragment
    }

    // Display the initial fragment with the list of posts
    private void displayInitialFragment(View view) {
        // Initialize RecyclerView for displaying posts
        regularCarsRecyclerView = view.findViewById(R.id.regularListingsRecyclerView);

        // Setup RecyclerView and load the list of posts
        setupRegularCarsRecyclerView();
        loadCarListings();

        // Setup Floating Action Button for posting new content
        ExtendedFloatingActionButton btnPost = view.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the first step for creating a new post
                goToFirstStep();
            }
        });
    }

    // Setup RecyclerView to display user's posts
    private void setupRegularCarsRecyclerView() {
        regularCarsAdapter = new RegularCarsAdapter(new RegularCarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarListModel item) {
                // Show options to edit or delete a post on item click
                CharSequence[] options = {"Edit", "Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Would you like to edit or delete this post?");
                builder.setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Navigate to edit the selected post
                        navigateToCarEditFragment(item);
                    } else if (which == 1) {
                        // Delete the selected post
                        deletePost(item);
                    }
                });
                builder.show();
            }
        });
        // Set up RecyclerView's layout manager and adapter
        regularCarsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        regularCarsRecyclerView.setAdapter(regularCarsAdapter);
    }

    // Delete a car listing from Firebase
    private void deletePost(CarListModel item) {
        // Initialize FirebaseDataHandler for data operations
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();

        // Get the carId of the listing you want to delete
        String carId = item.getCarId();

        // Delete the car listing using FirebaseDataHandler
        dataHandler.deleteCarListing(carId, new FirebaseDataHandler.SaveDataCallback() {
            @Override
            public void onSuccess() {
                // Handle successful deletion
                Log.d("DeleteCarListing", "Listing deleted successfully");
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle failure during deletion
                Log.e("DeleteCarListing", "Error deleting listing", exception);
            }
        });
    }

    // Navigate to the car edit fragment with the selected CarListModel
    private void navigateToCarEditFragment(CarListModel carModel) {
        // Update the SharedViewModel with the selected CarListModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.setCarListModel(carModel);

        // Get the child FragmentManager
        FragmentManager childFragmentManager = getChildFragmentManager();

        // Replace the displayed fragment with the car edit fragment
        if (childFragmentManager != null) {
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.post_fragment_container, new PostStepOneFragment());
            fragmentTransaction.commit();
        }
    }


    // Load the list of car listings from Firebase
    private void loadCarListings() {
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();
        dataHandler.fetchUserCarListings(new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carList) {
                // Update the RecyclerViews with the fetched car listings
                regularCarsAdapter.setCarListings(carList);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle the error when fetching car listings fails
                Log.e("HomeFragment", "Error fetching car listings", exception);
            }
        });
    }

    // Navigate to the first step fragment for posting a car
    private void goToFirstStep() {
        // Access the shared ViewModel to clear any existing car model data
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.clearCarModel();

        // Replace the displayed list of posts with the first step fragment
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager != null) {
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.post_fragment_container, new PostStepOneFragment());
            fragmentTransaction.commit();
        }
    }


    public void goToPreviousStep() {
        // Get the child FragmentManager associated with this fragment
        FragmentManager childFragmentManager = getChildFragmentManager();

        // Check if the FragmentManager is available and there are fragments in the back stack
        if (childFragmentManager != null && childFragmentManager.getBackStackEntryCount() > 0) {
            // Pop the top state off the back stack, effectively navigating back to the previous step
            childFragmentManager.popBackStack();
        } else { }
    }

    public void goToNextStep(Fragment nextStepFragment) {
        // Get the child FragmentManager associated with this fragment
        FragmentManager childFragmentManager = getChildFragmentManager();

        // Ensure the FragmentManager is available
        if (childFragmentManager != null) {
            // Begin a new FragmentTransaction for adding or replacing fragments
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();

            // Set custom animations for the fragment transition
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right, R.anim.exit_to_left, // Enter from right, exit to left animation
                    R.anim.enter_from_left, R.anim.exit_to_right // Enter from left, exit to right animation
            );

            // Replace the current fragment in the specified container with the nextStepFragment
            fragmentTransaction.replace(R.id.post_fragment_container, nextStepFragment);

            // Add this transaction to the back stack so it can be navigated back to
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();
        }
    }

}
