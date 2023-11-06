package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout mEmailLayout, mPasswordLayout;
    private EditText mPasswordField, mEmailField;
    private Button mSignUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mEmailLayout = findViewById(R.id.layoutEmail);
        mPasswordLayout = findViewById(R.id.layoutPassword);
        mSignUpButton = findViewById(R.id.buttonSignup);
        mPasswordField = findViewById(R.id.editTextPassword);
        mEmailField = findViewById(R.id.editTextEmail);

        mSignUpButton.setOnClickListener(view -> attemptSignUp());
    }

    private void attemptSignUp() {
        mEmailLayout.setError(null); // Reset error
        mPasswordLayout.setError(null); // Reset error

        String email = mEmailLayout.getEditText().getText().toString().trim();
        String password = mPasswordLayout.getEditText().getText().toString().trim();

        if (!validateForm(email, password)) {
            return;
        }

        // Show a loading indicator

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide loading indicator

                    if (task.isSuccessful()) {
                        navigateToMain();
                    } else {
                        // If sign-up fails, display a message to the user.
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            mEmailLayout.setError("This email address is already in use.");
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign-up failed: " +
                                    task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Required.");
            valid = false;
        } else {
            mEmailLayout.setError(null);
        }

        // Validate password
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

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in, navigate to the main activity
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // User sign-up failed, prompt the user to try again
            mPasswordField.setText("");  // Clear the password field
            // Optionally, clear the email field if you want the user to start over
            mEmailField.setText("");

            // Enable a 'try again' button or show additional messages if needed
            // For example:
            mSignUpButton.setEnabled(true);
            mSignUpButton.setText("Try Again");

            // Log the error or show a message
            // Log.e("SignUpActivity", "Sign-up failed.");
            // This is optional since you might be showing a toast already
        }
    }
}
