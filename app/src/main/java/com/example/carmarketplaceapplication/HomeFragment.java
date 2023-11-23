package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    private RecyclerView featuredCarsRecyclerView;
    private RecyclerView regularCarsRecyclerView;
    private FeaturedCarsAdapter featuredCarsAdapter;
    private RegularCarsAdapter regularCarsAdapter;
    private List<CarListModel> fullCarList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerViews
        featuredCarsRecyclerView = view.findViewById(R.id.featuredListingsRecyclerView);
        regularCarsRecyclerView = view.findViewById(R.id.regularListingsRecyclerView);

        // Setup the RecyclerViews
//        setupFeaturedCarsRecyclerView();
        setupRegularCarsRecyclerView();
        loadCarListings();

        // In HomeFragment
        Button filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> showFilterBottomSheet());

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

    private void showFilterBottomSheet() {
        FilterBottomSheetFragment filterFragment = FilterBottomSheetFragment.newInstance(this);
        filterFragment.show(getChildFragmentManager(), filterFragment.getTag());
    }

    @Override
    public void onFiltersApplied(FilterBottomSheetFragment.FilterParams filterParams) {
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();
        dataHandler.fetchFilteredCarListings(filterParams, new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carList) {
                // Update your RecyclerViews with the filtered list
                regularCarsAdapter.setCarListings(carList);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle errors
                Log.e("HomeFragment", "Error fetching filtered car listings", exception);
            }
        });
    }


    private void filterCarListings(String query) {
        // Filter the car listings based on the query
        List<CarListModel> filteredList = new ArrayList<>();
        for (CarListModel car : fullCarList) {
            if (car.getMake().toLowerCase().contains(query.toLowerCase()) || car.getModel().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(car);
            }
        }

        // Update your RecyclerViews with the filtered list
//        featuredCarsAdapter.setCarListings(filteredList);
        regularCarsAdapter.setCarListings(filteredList);
    }


    private void setupFeaturedCarsRecyclerView() {
        featuredCarsAdapter = new FeaturedCarsAdapter(new FeaturedCarsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarListModel item) {
                navigateToCarDetailFragment(item);
            }
        });
        featuredCarsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredCarsRecyclerView.setAdapter(featuredCarsAdapter);
    }

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

    private void loadCarListings() {
        FirebaseDataHandler dataHandler = new FirebaseDataHandler();
        dataHandler.fetchCarListings(new FirebaseDataHandler.FetchDataCallback() {
            @Override
            public void onSuccess(List<CarListModel> carList) {
                fullCarList = carList;
                // Update your RecyclerViews here
//                featuredCarsAdapter.setCarListings(carList); // Assuming setCarListings is a method in your adapter
                regularCarsAdapter.setCarListings(carList); // Same as above
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle the error
                Log.e("HomeFragment", "Error fetching car listings", exception);
            }
        });
    }

    private void navigateToCarDetailFragment(CarListModel carModel) {
        Fragment carDetailFragment = CarDetailFragment.newInstance(carModel);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, carDetailFragment) // 'fragment_container' is the ID of your FrameLayout or other container
                .addToBackStack(null) // Add this transaction to the back stack
                .commit();
    }
}
