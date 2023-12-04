package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout mEmailLayout, mPasswordLayout, mFirstNameLayout, mLastNameLayout, mAddressLayout, mPhoneLayout;
    private EditText mEmailField, mPasswordField, mFirstNameField, mLastNameField, mPhoneField;
    private AutoCompleteTextView mAddressField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize all TextInput layouts
        mFirstNameLayout = findViewById(R.id.layout_first_name);
        mLastNameLayout = findViewById(R.id.layout_last_name);
        mEmailLayout = findViewById(R.id.layout_email);
        mPhoneLayout = findViewById(R.id.layout_phone_number);
        mPasswordLayout = findViewById(R.id.layout_password);

        // Initialize all EditText fields
        mFirstNameField = findViewById(R.id.editText_first_name);
        mLastNameField = findViewById(R.id.editText_last_name);
        mEmailField = findViewById(R.id.editText_email);
        mPhoneField = findViewById(R.id.editText_phone_number);
        mPasswordField = findViewById(R.id.editText_password);

        Button mSignUpButton = findViewById(R.id.button_sign_up);
        TextView mLoginTextView = findViewById(R.id.textView_login);

        // Set onClickListeners for sign-up and login
        mSignUpButton.setOnClickListener(view -> attemptSignUp());
        mLoginTextView.setOnClickListener(view -> navigateToLogin());
    }

    private void attemptSignUp() {
        // Validate form fields
        boolean validForm = validateForm(
                mFirstNameField.getText().toString().trim(),
                mLastNameField.getText().toString().trim(),
                mEmailField.getText().toString().trim(),
                mPasswordField.getText().toString().trim(),
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
                        UserProfileModel userProfile = new UserProfileModel(
                                mFirstNameField.getText().toString().trim(),
                                mLastNameField.getText().toString().trim(),
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

    private boolean validateForm(String firstName, String lastName, String email, String password, String phone) {
        boolean valid = true;

        // Validate the new fields
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameLayout.setError("Please enter a first name.");
            valid = false;
        } else {
            mFirstNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastNameLayout.setError("Please enter a last name.");
            valid = false;
        } else {
            mLastNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneLayout.setError("Please enter a phone number.");
            valid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            mPhoneLayout.setError("Please enter a valid phone number.");
            valid = false;
        } else {
            mPhoneLayout.setError(null);
        }

        // Existing validation
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Please enter an email address.");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailLayout.setError("Please enter a valid email address.");
            valid = false;
        } else {
            mEmailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("Please enter a password.");
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
