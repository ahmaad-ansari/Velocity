package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    // Declaring UI elements and Firebase instances used in the profile fragment
    private EditText mFirstNameField, mLastNameField, mEmailField, mPhoneNumberField;
    private FloatingActionButton mEditProfileButton, mLogoutButton, mDeleteButton;
    private Button mSaveProfileButton, mCancelEditsButton;
    private ImageView imageViewProfilePicture;

    // Model to store original user data
    private UserProfileModel originalUserData;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;


    // This method sets up the view for the profile fragment. It initializes various UI components such as EditTexts, buttons, and image views.
    // It also sets click listeners for buttons like Edit Profile, Cancel, Save, Delete Account, and Logout.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initializing Firebase authentication and Firestore instances
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initializing EditTexts and other UI elements
        mFirstNameField = view.findViewById(R.id.editTextFirstName);
        mLastNameField = view.findViewById(R.id.editTextLastName);
        mEmailField = view.findViewById(R.id.editTextEmail);
        mPhoneNumberField = view.findViewById(R.id.editTextPhoneNumber);
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);
        mEditProfileButton = view.findViewById(R.id.buttonEditProfile);
        mSaveProfileButton = view.findViewById(R.id.buttonSaveProfile);
        mCancelEditsButton = view.findViewById(R.id.buttonCancel);
        mLogoutButton = view.findViewById(R.id.buttonLogout);
        mDeleteButton = view.findViewById(R.id.buttonDeleteAccount);

        // Setting click listeners for buttons
        mEditProfileButton.setOnClickListener(view1 -> enableEditMode());
        mCancelEditsButton.setOnClickListener(view13 -> cancelEdits());
        mSaveProfileButton.setOnClickListener(view12 -> saveProfile());
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deleting user account and redirecting to login/signup page
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DeleteAccount", "User account deleted.");
                                        if (getActivity() != null) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            getActivity().startActivity(intent);
                                            getActivity().finish();
                                        }
                                    } else {
                                        Log.e("DeleteAccount", "Failed to delete user.", task.getException());
                                        if (getActivity() != null) {
                                            Toast.makeText(getActivity(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        // Setting a click listener for the logout button
        mLogoutButton.setOnClickListener(view14 -> {
            mAuth.signOut();
            navigateToLogin();
        });

        // Loading user profile data
        loadUserProfile();

        return view;
    }

    // This method enables the editing mode for EditText fields by setting them as editable.
    // It also makes the 'Cancel' and 'Save' buttons visible to allow users to discard or save changes.
    private void enableEditMode() {
        setEditTextsEditable(true); // Make EditTexts editable
        mCancelEditsButton.setVisibility(View.VISIBLE); // Show the 'Cancel' button
        mSaveProfileButton.setVisibility(View.VISIBLE); // Show the 'Save' button
    }

    // This method navigates the user to the login page by creating an intent to launch LoginActivity.
    // It clears the activity stack to prevent returning to this screen after logging out.
    private void navigateToLogin() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent); // Start the LoginActivity
        requireActivity().finish(); // Finish the current activity
    }

    private void saveProfile() {
        // Construct a map to hold updated user profile information
        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("firstName", mFirstNameField.getText().toString().trim());
        updatedProfile.put("lastName", mLastNameField.getText().toString().trim());
        updatedProfile.put("email", mEmailField.getText().toString().trim());
        updatedProfile.put("phoneNumber", mPhoneNumberField.getText().toString().trim());

        // Validate the form data
        if (!validateForm(updatedProfile)) {
            return; // Exit method if validation fails
        }

        // Save the new data to Firestore if there's a logged-in user
        if (currentUser != null) {
            mFirestore.collection("users").document(currentUser.getUid())
                    .update(updatedProfile) // Update Firestore document with the new profile data
                    .addOnSuccessListener(aVoid -> {
                        // On success, show a success message, hide buttons, and disable editing
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        setEditTextsEditable(false);
                        mCancelEditsButton.setVisibility(View.GONE);
                        mSaveProfileButton.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        // On failure, show an error message
                        Toast.makeText(requireContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void cancelEdits() {
        // Revert the data in the input fields to the original data if available
        if (originalUserData != null) {
            mFirstNameField.setText(originalUserData.getFirstName());
            mLastNameField.setText(originalUserData.getLastName());
            mEmailField.setText(originalUserData.getEmail());
            mPhoneNumberField.setText(originalUserData.getPhoneNumber());
        }

        // Hide the "Cancel" and "Save" buttons, and show the "Edit Profile" button
        mCancelEditsButton.setVisibility(View.GONE);
        mSaveProfileButton.setVisibility(View.GONE);

        // Optionally, disable the fields to prevent editing
        setEditTextsEditable(false);
    }

    private void setEditTextsEditable(boolean editable) {
        // Enable or disable EditText fields based on the 'editable' flag
        mFirstNameField.setEnabled(editable);
        mLastNameField.setEnabled(editable);
        mEmailField.setEnabled(editable);
        mPhoneNumberField.setEnabled(editable);
    }

    private boolean validateForm(Map<String, Object> updatedProfile) {
        boolean valid = true;

        // Validation for First Name
        String firstName = (String) updatedProfile.get("firstName");
        if (firstName.isEmpty()) {
            mFirstNameField.setError("First name is required."); // Set error message for empty first name
            valid = false; // Set validation to false
        } else {
            mFirstNameField.setError(null); // Clear error message
        }

        // Validation for Last Name
        String lastName = (String) updatedProfile.get("lastName");
        if (lastName.isEmpty()) {
            mLastNameField.setError("Last name is required."); // Set error message for empty last name
            valid = false; // Set validation to false
        } else {
            mLastNameField.setError(null); // Clear error message
        }

        // Validation for Email
        String email = (String) updatedProfile.get("email");
        if (email.isEmpty()) {
            mEmailField.setError("Email is required."); // Set error message for empty email
            valid = false; // Set validation to false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Please enter a valid email address."); // Set error message for invalid email format
            valid = false; // Set validation to false
        } else {
            mEmailField.setError(null); // Clear error message
        }

        // Validation for Phone Number
        String phoneNumber = (String) updatedProfile.get("phoneNumber");
        if (phoneNumber.isEmpty()) {
            mPhoneNumberField.setError("Phone number is required."); // Set error message for empty phone number
            valid = false; // Set validation to false
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            mPhoneNumberField.setError("Please enter a valid phone number."); // Set error message for invalid phone number format
            valid = false; // Set validation to false
        } else {
            mPhoneNumberField.setError(null); // Clear error message
        }

        return valid; // Return validation status
    }

    private void loadUserProfile() {
        // Check if there's a current user
        if (currentUser != null) {
            // Retrieve user data from Firestore
            mFirestore.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        // Check if the task to retrieve data is successful
                        if (task.isSuccessful()) {
                            // Get the document snapshot
                            DocumentSnapshot document = task.getResult();
                            // Check if the document exists and has data
                            if (document != null && document.exists()) {
                                // Populate UI fields with user data
                                mFirstNameField.setText(document.getString("firstName"));
                                mLastNameField.setText(document.getString("lastName"));
                                mEmailField.setText(document.getString("email"));
                                mPhoneNumberField.setText(document.getString("phoneNumber"));

                                // Create a UserProfileModel instance with original data
                                originalUserData = new UserProfileModel(document.getString("firstName"), document.getString("lastName"), document.getString("email"), document.getString("phoneNumber"), null, null);

                                // Set image from URL using your preferred image loading library
                                // Glide.with(requireContext()).load(document.getString("profileImageUrl")).into(imageViewProfilePicture);

                                // Initially make EditTexts not editable
                                setEditTextsEditable(false);
                            } else {
                                // Document doesn't exist, show a message
                                Toast.makeText(requireContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Show an error message if failed to load user data
                            Toast.makeText(requireContext(), "Failed to load user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
