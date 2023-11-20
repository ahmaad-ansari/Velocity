package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList; // Use your data model imports here
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView featuredCarsRecyclerView;
    private RecyclerView regularCarsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerViews
        featuredCarsRecyclerView = view.findViewById(R.id.featuredListingsRecyclerView);
        regularCarsRecyclerView = view.findViewById(R.id.regularListingsRecyclerView);

        // Setup the RecyclerViews
        setupFeaturedCarsRecyclerView();
        setupRegularCarsRecyclerView();

        return view;
    }

    private void setupFeaturedCarsRecyclerView() {
        // Create a list of featured car listings
        List<CarListModel> featuredCarListings = getDummyFeaturedCarListings();

        // Create an instance of FeaturedCarsAdapter
        FeaturedCarsAdapter featuredCarsAdapter = new FeaturedCarsAdapter(featuredCarListings, new FeaturedCarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarListModel item) {
                // Implement your action on item click
                // For example, navigate to a detailed car view
            }
        });

        // Set the adapter and layout manager for the RecyclerView
        featuredCarsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredCarsRecyclerView.setAdapter(featuredCarsAdapter);
    }

    private void setupRegularCarsRecyclerView() {
        // Create a list of regular car listings
        List<CarListModel> regularCarListings = getDummyRegularCarListings();

        // Create an instance of RegularCarsAdapter
        RegularCarsAdapter regularCarsAdapter = new RegularCarsAdapter(regularCarListings, new RegularCarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarListModel item) {
                // Implement your action on item click
                // For example, navigate to a detailed car view
            }
        });

        // Set the adapter and layout manager for the RecyclerView
        regularCarsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        regularCarsRecyclerView.setAdapter(regularCarsAdapter);
    }

    // Dummy methods for fetching car listings data - replace these with your actual data fetching logic
    private List<CarListModel> getDummyFeaturedCarListings() {
        List<CarListModel> featuredListings = new ArrayList<>();

        // Add dummy data for featured listings
        for (int i = 0; i < 10; i++) {
            featuredListings.add(new CarListModel(
                    "1", "Ferrari", "488 Spider", 2020, "Red", 10000, 250000,
                    "A stunning sports convertible in excellent condition.",
                    null, // Replace with actual image URLs
                    null, "John Doe", "123456789", "johndoe@example.com",
                    "", "Automatic", "","Gasoline", 2, 2, true, true, true, true
            ));
        }

        return featuredListings;
    }

    private List<CarListModel> getDummyRegularCarListings() {
        List<CarListModel> regularListings = new ArrayList<>();

        // Add dummy data for regular listings
        for (int i = 0; i < 10; i++) {
            regularListings.add(new CarListModel(
                    "2", "Toyota", "RAV4", 2019, "Blue", 30000, 20000,
                    "Reliable family SUV with great fuel efficiency.",
                    null, // Replace with actual image URLs
                    null, "Jane Smith", "987654321", "janesmith@example.com",
                    "", "Automatic", "","Diesel", 4, 5, true, false, true, false
            ));
        }
        // Add more dummy listings as needed

        return regularListings;
    }

}
