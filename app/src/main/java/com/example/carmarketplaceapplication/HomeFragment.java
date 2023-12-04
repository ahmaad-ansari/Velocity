package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

// Fragment displaying car listings and providing filtering functionality
public class HomeFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    // RecyclerViews and adapters for displaying car listings
    private RecyclerView regularCarsRecyclerView;
    private RegularCarsAdapter regularCarsAdapter;
    private List<CarListModel> fullCarList = new ArrayList<>();
    private List<String> videoUrls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerViews
        regularCarsRecyclerView = view.findViewById(R.id.regularListingsRecyclerView);

        // Setup the RecyclerViews
        setupRegularCarsRecyclerView();

        // Load initial car listings
        loadCarListings();

        // Set up filter button
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> showFilterBottomSheet());

        // Set up search bar text listener for filtering
        EditText searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filterCarListings(editable.toString());
            }
        });

        return view;
    }

    // Show the filter bottom sheet to apply filters
    private void showFilterBottomSheet() {
        FilterBottomSheetFragment filterFragment = FilterBottomSheetFragment.newInstance(this);
        filterFragment.show(getChildFragmentManager(), filterFragment.getTag());
    }

    // Callback method when filters are applied
    @Override
    public void onFiltersApplied(FilterBottomSheetFragment.FilterParams filterParams) {
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();
        dataHandler.fetchFilteredCarListings(filterParams, new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carList) {
                // Update RecyclerViews with the filtered list
                regularCarsAdapter.setCarListings(carList);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle errors in fetching filtered car listings
                Log.e("HomeFragment", "Error fetching filtered car listings", exception);
            }
        });
    }

    // Filter car listings based on a search query
    private void filterCarListings(String query) {
        List<CarListModel> filteredList = new ArrayList<>();
        for (CarListModel car : fullCarList) {
            if (car.getMake().toLowerCase().contains(query.toLowerCase()) || car.getModel().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(car);
            }
        }
        regularCarsAdapter.setCarListings(filteredList);
    }

    // Initialize RecyclerView for regular car listings
    private void setupRegularCarsRecyclerView() {
        regularCarsAdapter = new RegularCarsAdapter(new RegularCarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarListModel item) {
                navigateToCarDetailFragment(item);
            }
        });
        regularCarsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        regularCarsRecyclerView.setAdapter(regularCarsAdapter);
    }

    // Load car listings from Firebase
    private void loadCarListings() {
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();
        dataHandler.fetchCarListings(new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carList) {
                fullCarList = carList;
                // Update RecyclerViews with the loaded car listings
                regularCarsAdapter.setCarListings(carList);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle errors in fetching car listings
                Log.e("HomeFragment", "Error fetching car listings", exception);
            }
        });
    }

    // Navigate to the car detail fragment upon selecting a car
    private void navigateToCarDetailFragment(CarListModel carModel) {
        Fragment carDetailFragment = CarDetailFragment.newInstance(carModel);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, carDetailFragment) // Use replace instead of add
                .addToBackStack(null)
                .commit();
    }
}


