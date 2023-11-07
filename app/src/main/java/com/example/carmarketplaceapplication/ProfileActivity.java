package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
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

    private EditText mFirstNameField, mLastNameField, mEmailField, mPhoneNumberField;
    private AutoCompleteTextView mAddressField;

    private Button mEditProfileButton, mSaveProfileButton, mCancelEditsButton, mLogoutButton;
    private ImageView imageViewProfilePicture;

    private UserProfile originalUserData;
    private AddressAutocompleteHelper addressAutocompleteHelper;


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
        mFirstNameField = findViewById(R.id.editTextFirstName);
        mLastNameField = findViewById(R.id.editTextLastName);
        mEmailField = findViewById(R.id.editTextEmail);
        mAddressField = findViewById(R.id.autoCompleteTextViewAddress);
        mPhoneNumberField = findViewById(R.id.editTextPhoneNumber);

        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);

        mEditProfileButton = findViewById(R.id.buttonEditProfile);
        mSaveProfileButton = findViewById(R.id.buttonSaveProfile);
        mCancelEditsButton = findViewById(R.id.buttonCancel);
        mLogoutButton = findViewById(R.id.buttonLogout);

        mEditProfileButton.setOnClickListener(view -> enableEditMode());
        mSaveProfileButton.setOnClickListener(view -> saveProfile());
        mCancelEditsButton.setOnClickListener(view -> cancelEdits());

        mLogoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            navigateToLogin();
        });

        loadUserProfile();
    }

    private void enableEditMode() {
        // Make EditTexts editable
        setEditTextsEditable(true);
        addressAutocompleteHelper = new AddressAutocompleteHelper(this, mAddressField);

        mSaveProfileButton.setVisibility(View.VISIBLE);
        mCancelEditsButton.setVisibility(View.VISIBLE);
        mEditProfileButton.setVisibility(View.GONE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveProfile() {
        // Construct a map to hold updated user profile information
        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("firstName", mFirstNameField.getText().toString().trim());
        updatedProfile.put("lastName", mLastNameField.getText().toString().trim());
        updatedProfile.put("email", mEmailField.getText().toString().trim());
        updatedProfile.put("address", mAddressField.getText().toString().trim());
        updatedProfile.put("phoneNumber", mPhoneNumberField.getText().toString().trim());

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
                        mSaveProfileButton.setVisibility(View.GONE);
                        mCancelEditsButton.setVisibility(View.GONE);
                        mEditProfileButton.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void cancelEdits() {
        // Revert the data in the input fields to the original data
        if (originalUserData != null) {
            mFirstNameField.setText(originalUserData.getFirstName());
            mLastNameField.setText(originalUserData.getLastName());
            mEmailField.setText(originalUserData.getEmail());
            mAddressField.setText(originalUserData.getAddress());
            mPhoneNumberField.setText(originalUserData.getPhoneNumber());
        }

        // Hide the "Cancel" and "Save" buttons, and show the "Edit Profile" button
        mSaveProfileButton.setVisibility(View.GONE);
        mCancelEditsButton.setVisibility(View.GONE);
        mEditProfileButton.setVisibility(View.VISIBLE);

        // Optionally, disable the fields to prevent editing
        setEditTextsEditable(false);
    }


    private void setEditTextsEditable(boolean editable) {
        mFirstNameField.setEnabled(editable);
        mLastNameField.setEnabled(editable);
        mEmailField.setEnabled(editable);
        mAddressField.setEnabled(editable);
        mPhoneNumberField.setEnabled(editable);
    }

    private boolean validateForm(Map<String, Object> updatedProfile) {
        boolean valid = true;

        // Validation for First Name
        String firstName = (String) updatedProfile.get("firstName");
        if (firstName.isEmpty()) {
            mFirstNameField.setError("First name is required.");
            valid = false;
        } else {
            mFirstNameField.setError(null);
        }

        // Validation for Last Name
        String lastName = (String) updatedProfile.get("lastName");
        if (lastName.isEmpty()) {
            mLastNameField.setError("Last name is required.");
            valid = false;
        } else {
            mLastNameField.setError(null);
        }

        // Validation for Email
        String email = (String) updatedProfile.get("email");
        if (email.isEmpty()) {
            mEmailField.setError("Email is required.");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Please enter a valid email address.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        // Validation for City
        String city = (String) updatedProfile.get("city");
        if (city.isEmpty()) {
            mAddressField.setError("Address is required.");
            valid = false;
        } else {
            mAddressField.setError(null);
        }

        // Validation for Phone Number
        String phoneNumber = (String) updatedProfile.get("phoneNumber");
        if (phoneNumber.isEmpty()) {
            mPhoneNumberField.setError("Phone number is required.");
            valid = false;
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            mPhoneNumberField.setError("Please enter a valid phone number.");
            valid = false;
        } else {
            mPhoneNumberField.setError(null);
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
                                mFirstNameField.setText(document.getString("firstName"));
                                mLastNameField.setText(document.getString("lastName"));
                                mEmailField.setText(document.getString("email"));
                                mAddressField.setText(document.getString("address"));
                                mPhoneNumberField.setText(document.getString("phoneNumber"));

                                originalUserData = new UserProfile(document.getString("firstName"), document.getString("lastName"), document.getString("address"), document.getString("email"), document.getString("phoneNumber"), null, null);
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
