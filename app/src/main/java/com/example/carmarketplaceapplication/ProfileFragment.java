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

    private EditText mFirstNameField, mLastNameField, mEmailField, mPhoneNumberField;
    private FloatingActionButton mEditProfileButton, mLogoutButton, mDeleteButton;
    private Button mSaveProfileButton, mCancelEditsButton;
    private ImageView imageViewProfilePicture;

    private UserProfileModel originalUserData;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize EditTexts
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

        mEditProfileButton.setOnClickListener(view1 -> enableEditMode());
        mCancelEditsButton.setOnClickListener(view13 -> cancelEdits());
        mSaveProfileButton.setOnClickListener(view12 -> saveProfile());

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("DeleteAccount", "User account deleted.");
                                // Redirect to login/signup page or close the app
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



        mLogoutButton.setOnClickListener(view14 -> {
            mAuth.signOut();
            navigateToLogin();
        });

        loadUserProfile();

        return view;
    }

    private void enableEditMode() {
        // Make EditTexts editable
        setEditTextsEditable(true);
        mCancelEditsButton.setVisibility(View.VISIBLE);
        mSaveProfileButton.setVisibility(View.VISIBLE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void saveProfile() {
        // Construct a map to hold updated user profile information
        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("firstName", mFirstNameField.getText().toString().trim());
        updatedProfile.put("lastName", mLastNameField.getText().toString().trim());
        updatedProfile.put("email", mEmailField.getText().toString().trim());
        updatedProfile.put("phoneNumber", mPhoneNumberField.getText().toString().trim());

        if (!validateForm(updatedProfile)) {
            return;
        }

        // Save the new data to Firestore
        if (currentUser != null) {
            mFirestore.collection("users").document(currentUser.getUid())
                    .update(updatedProfile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        setEditTextsEditable(false);
                        mCancelEditsButton.setVisibility(View.GONE);
                        mSaveProfileButton.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void cancelEdits() {
        // Revert the data in the input fields to the original data
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
                                mPhoneNumberField.setText(document.getString("phoneNumber"));

                                originalUserData = new UserProfileModel(document.getString("firstName"), document.getString("lastName"), document.getString("email"), document.getString("phoneNumber"), null, null);
                                // Set image from URL using your preferred image loading library
                                // Glide.with(requireContext()).load(document.getString("profileImageUrl")).into(imageViewProfilePicture);

                                // Initially make EditTexts not editable
                                setEditTextsEditable(false);
                            } else {
                                Toast.makeText(requireContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
