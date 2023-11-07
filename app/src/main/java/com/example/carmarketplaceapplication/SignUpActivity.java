package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout mEmailLayout, mPasswordLayout, mFirstNameLayout, mLastNameLayout, mCityLayout, mProvinceLayout, mPhoneLayout;
    private EditText mEmailField, mPasswordField, mFirstNameField, mLastNameField, mCityField, mProvinceField, mPhoneField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize all TextInputLayout and EditText fields
        mFirstNameLayout = findViewById(R.id.layoutFirstName);
        mLastNameLayout = findViewById(R.id.layoutLastName);
        mEmailLayout = findViewById(R.id.layoutEmail);
        mPasswordLayout = findViewById(R.id.layoutPassword);
        mCityLayout = findViewById(R.id.layoutCity);
        mProvinceLayout = findViewById(R.id.layoutProvince);
        mPhoneLayout = findViewById(R.id.layoutPhoneNumber);
        // ... Other layout initializations ...

        // Initialize all EditText fields
        mFirstNameField = findViewById(R.id.editTextFirstName);
        mLastNameField = findViewById(R.id.editTextLastName);
        mEmailField = findViewById(R.id.editTextEmail);
        mPasswordField = findViewById(R.id.editTextPassword);
        mCityField = findViewById(R.id.editTextCity);
        mProvinceField = findViewById(R.id.editTextProvince);
        mPhoneField = findViewById(R.id.editTextPhoneNumber);
        // ... Other EditText initializations ...

        Button mSignUpButton = findViewById(R.id.buttonSignup);
        Button mLoginButton = findViewById(R.id.buttonNavigateToLogin);

        mSignUpButton.setOnClickListener(view -> attemptSignUp());
        mLoginButton.setOnClickListener(view -> navigateToLogin());
    }

    private void attemptSignUp() {
        boolean validForm = validateForm(
                mFirstNameField.getText().toString().trim(),
                mLastNameField.getText().toString().trim(),
                mEmailField.getText().toString().trim(),
                mPasswordField.getText().toString().trim(),
                mCityField.getText().toString().trim(),
                mProvinceField.getText().toString().trim(),
                mPhoneField.getText().toString().trim()
        );

        if (!validForm) {
            return;
        }

        // Show a loading indicator

        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide loading indicator

                    if (task.isSuccessful()) {
                        // Save additional fields in Firestore
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserProfile userProfile = new UserProfile(
                                mFirstNameField.getText().toString().trim(),
                                mLastNameField.getText().toString().trim(),
                                mCityField.getText().toString().trim(),
                                mProvinceField.getText().toString().trim(),
                                firebaseUser.getEmail(),
                                mPhoneField.getText().toString().trim(), // Assuming you are collecting the phone number here
                                "", // ProfileImageUrl can be set after uploading an image
                                firebaseUser.getUid()
                        );

                        mFirestore.collection("users").document(firebaseUser.getUid())
                                .set(userProfile)
                                .addOnSuccessListener(aVoid -> navigateToMain())
                                .addOnFailureListener(e -> {
                                    // Handle the error, e.g., show a toast
                                    Toast.makeText(SignUpActivity.this, "Error saving user profile: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        // If sign-up fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateForm(String firstName, String lastName, String email, String password, String city, String province, String phone) {
        boolean valid = true;

        // Validate the new fields
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameLayout.setError("Required.");
            valid = false;
        } else {
            mFirstNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastNameLayout.setError("Required.");
            valid = false;
        } else {
            mLastNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(city)) {
            mCityLayout.setError("Required.");
            valid = false;
        } else {
            mCityLayout.setError(null);
        }

        if (TextUtils.isEmpty(province)) {
            mProvinceLayout.setError("Required.");
            valid = false;
        } else {
            mProvinceLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneLayout.setError("Required.");
            valid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            mPhoneLayout.setError("Please enter a valid phone number.");
            valid = false;
        } else {
            mPhoneLayout.setError(null);
        }

        // Existing validation
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Required.");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailLayout.setError("Please enter a valid email address.");
            valid = false;
        } else {
            mEmailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("Required.");
            valid = false;
        } else if (password.length() < 6) {
            mPasswordLayout.setError("Password must be at least 6 characters.");
            valid = false;
        } else {
            mPasswordLayout.setError(null);
        }

        return valid;
    }

    private void navigateToMain() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }

    private void navigateToLogin() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }
}
