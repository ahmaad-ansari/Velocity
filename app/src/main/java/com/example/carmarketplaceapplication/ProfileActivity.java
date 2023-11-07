package com.example.carmarketplaceapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    // Remove the TextView declarations as they are no longer used

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextCity, editTextProvince, editTextPhoneNumber;
    private Button buttonEditProfile, buttonSaveProfile;
    private ImageView imageViewProfilePicture;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize EditTexts
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);

        buttonEditProfile.setOnClickListener(view -> enableEditMode());
        buttonSaveProfile.setOnClickListener(view -> saveProfile());

        loadUserProfile();
    }

    private void enableEditMode() {
        // Make EditTexts editable
        setEditTextsEditable(true);

        buttonSaveProfile.setVisibility(View.VISIBLE);
        buttonEditProfile.setVisibility(View.GONE);
    }

    private void saveProfile() {
        // Construct a map to hold updated user profile information
        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("firstName", editTextFirstName.getText().toString().trim());
        updatedProfile.put("lastName", editTextLastName.getText().toString().trim());
        updatedProfile.put("email", editTextEmail.getText().toString().trim());
        updatedProfile.put("city", editTextCity.getText().toString().trim());
        updatedProfile.put("province", editTextProvince.getText().toString().trim());
        updatedProfile.put("phoneNumber", editTextPhoneNumber.getText().toString().trim());

        if (!validateForm(updatedProfile)) {
            return;
        }

        // Save the new data to Firestore
        if (currentUser != null) {
            mFirestore.collection("users").document(currentUser.getUid())
                    .update(updatedProfile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        setEditTextsEditable(false);
                        buttonSaveProfile.setVisibility(View.GONE);
                        buttonEditProfile.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void setEditTextsEditable(boolean editable) {
        editTextFirstName.setEnabled(editable);
        editTextLastName.setEnabled(editable);
        editTextEmail.setEnabled(editable);
        editTextCity.setEnabled(editable);
        editTextProvince.setEnabled(editable);
        editTextPhoneNumber.setEnabled(editable);
    }

    private boolean validateForm(Map<String, Object> updatedProfile) {
        boolean valid = true;

        // Validation for First Name
        String firstName = (String) updatedProfile.get("firstName");
        if (firstName.isEmpty()) {
            editTextFirstName.setError("First name is required.");
            valid = false;
        } else {
            editTextFirstName.setError(null);
        }

        // Validation for Last Name
        String lastName = (String) updatedProfile.get("lastName");
        if (lastName.isEmpty()) {
            editTextLastName.setError("Last name is required.");
            valid = false;
        } else {
            editTextLastName.setError(null);
        }

        // Validation for Email
        String email = (String) updatedProfile.get("email");
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required.");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address.");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        // Validation for City
        String city = (String) updatedProfile.get("city");
        if (city.isEmpty()) {
            editTextCity.setError("City is required.");
            valid = false;
        } else {
            editTextCity.setError(null);
        }

        // Validation for Province
        String province = (String) updatedProfile.get("province");
        if (province.isEmpty()) {
            editTextProvince.setError("Province is required.");
            valid = false;
        } else {
            editTextProvince.setError(null);
        }

        // Validation for Phone Number
        String phoneNumber = (String) updatedProfile.get("phoneNumber");
        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Phone number is required.");
            valid = false;
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            editTextPhoneNumber.setError("Please enter a valid phone number.");
            valid = false;
        } else {
            editTextPhoneNumber.setError(null);
        }

        return valid;
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            mFirestore.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                editTextFirstName.setText(document.getString("firstName"));
                                editTextLastName.setText(document.getString("lastName"));
                                editTextEmail.setText(document.getString("email"));
                                editTextCity.setText(document.getString("city"));
                                editTextProvince.setText(document.getString("province"));
                                editTextPhoneNumber.setText(document.getString("phoneNumber"));
                                // Set image from URL using your preferred image loading library
                                // Glide.with(this).load(document.getString("profileImageUrl")).into(imageViewProfilePicture);

                                // Initially make EditTexts not editable
                                setEditTextsEditable(false);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to load user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
