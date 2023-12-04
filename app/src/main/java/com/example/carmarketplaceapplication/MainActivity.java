package com.example.carmarketplaceapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Open the default fragment initially
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    viewModel.clearImageSources();
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_post) {
                    selectedFragment = new PostFragment();
                } else if (item.getItemId() == R.id.nav_map) {
                    viewModel.clearImageSources();
                    selectedFragment = new MapFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    viewModel.clearImageSources();
                    selectedFragment = new ProfileFragment();
                } else {
                    // Handle other menu items
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };

    public void showHomeFragment() {
        viewModel.clearImageSources();
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment) // 'fragment_container' is your FrameLayout ID in your activity layout where fragments are placed
                .commit();
    }

    public void showPostFragment() {
        viewModel.clearImageSources();
        PostFragment postFragment = new PostFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, postFragment) // 'fragment_container' is your FrameLayout ID in your activity layout where fragments are placed
                .commit();
    }
}
