package com.example.carmarketplaceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * Activity for user login.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mEmailLayout, mPasswordLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Bind layout elements
        mEmailLayout = findViewById(R.id.textInputLayout_email);
        mPasswordLayout = findViewById(R.id.textInputLayout_password);
        Button mLoginButton = findViewById(R.id.button_login);
        TextView mRegisterTextView = findViewById(R.id.textView_register);

        // Set click listeners for login and register text
        mRegisterTextView.setOnClickListener(view -> navigateToSignUp());
        mLoginButton.setOnClickListener(view -> attemptLogin());
    }

    /**
     * Attempt to perform user login.
     */
    private void attemptLogin() {
        mEmailLayout.setError(null); // Reset error
        mPasswordLayout.setError(null); // Reset error

        String email = mEmailLayout.getEditText().getText().toString().trim();
        String password = mPasswordLayout.getEditText().getText().toString().trim();

        if (!validateForm(email, password)) {
            return;
        }

        // Show a loading indicator

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide loading indicator

                    if (task.isSuccessful()) {
                        navigateToMain();
                    } else {
                        // Handle login failure scenarios
                        handleLoginFailure(task);
                    }
                });
    }

    /**
     * Validate email and password fields.
     */
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
        } else {
            mPasswordLayout.setError(null);
        }

        return valid;
    }

    /**
     * Handle different login failure scenarios.
     */
    private void handleLoginFailure(Task<AuthResult> task) {
        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
            mPasswordLayout.setError("Invalid password. Try again.");
        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
            mEmailLayout.setError("No account found with this email.");
        } else {
            // Show a general login failure message
            Toast.makeText(LoginActivity.this, "Login failed: " +
                    task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Navigate to the main activity upon successful login.
     */
    private void navigateToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Navigate to the sign-up activity.
     */
    private void navigateToSignUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }
}