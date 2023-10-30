package com.example.carmarketplaceapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private TextInputLayout usernameLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    private Button registerButton;
    private GenericTextWatcher emailWatcher, passwordWatcher, confirmPasswordWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupSignupPrompt();

        // Initialize the fields and layouts
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);

        usernameLayout = findViewById(R.id.username_layout);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);

        // Initialize generic TextWatchers
        GenericTextWatcher usernameWatcher = new GenericTextWatcher(usernameLayout, username -> !username.isEmpty(), getString(R.string.error_required_username));
        GenericTextWatcher emailWatcher = new GenericTextWatcher(emailLayout, this::isValidEmail, getString(R.string.error_invalid_email));
        GenericTextWatcher passwordWatcher = new GenericTextWatcher(passwordLayout, this::isValidPassword, getString(R.string.error_weak_password));
        GenericTextWatcher confirmPasswordWatcher = new GenericTextWatcher(confirmPasswordLayout, password -> password.equals(passwordInput.getText().toString()), getString(R.string.error_passwords_mismatch));

        usernameInput.addTextChangedListener(usernameWatcher);
        emailInput.addTextChangedListener(emailWatcher);
        passwordInput.addTextChangedListener(passwordWatcher);
        confirmPasswordInput.addTextChangedListener(confirmPasswordWatcher);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areFieldsValid()) {
                    // If all fields are valid, proceed with the registration process.
                    performRegistration();
                } else {
                    // If some fields are invalid, display appropriate errors.
                    // The individual field checks inside areFieldsValid() should handle this.
                }
            }
        });
    }

    private void setupSignupPrompt() {
        TextView signupText = findViewById(R.id.login_text);

        SpannableString spannable = new SpannableString("Already have an account? Login now!");

        // Color for the "Sign up now!" part
        int linkColor = ContextCompat.getColor(this, R.color.link_text);
        spannable.setSpan(
                new ForegroundColorSpan(linkColor),
                25,  // start index
                35,  // end index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Making the "Register now!" part clickable
        spannable.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },
                25,  // start index
                35,  // end index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        signupText.setText(spannable);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());  // To make the clickable span responsive
    }

    // Utility method to validate email format
    private boolean isValidEmail(String email) {
        // Simple validation for demonstration, consider using more robust validation
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Utility method to validate password strength
    private boolean isValidPassword(String password) {
        // Basic check for demonstration, consider expanding for production use
        return password != null && password.length() >= 8; // Minimum of 8 characters
    }

    private boolean areFieldsValid() {
        // Retrieve the values from the input fields
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Flags indicating whether fields are valid
        boolean isUsernameValid = true, isEmailValid = true, isPasswordValid = true, isConfirmPasswordValid = true;

        // Check if the fields are empty and set appropriate error messages
        if (username.isEmpty()) {
            usernameLayout.setError(getString(R.string.error_required_username));
            isUsernameValid = false;
        } else {
            usernameLayout.setError(null); // Clear previous error
            usernameLayout.setErrorEnabled(false);
        }

        if (email.isEmpty()) {
            emailLayout.setError(getString(R.string.error_required_email));
            isEmailValid = false;
        } else if (!isValidEmail(email)) { // Your method to check for a valid email pattern
            emailLayout.setError(getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            emailLayout.setError(null); // Clear previous error
            emailLayout.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.error_required_password));
            isPasswordValid = false;
        } else if (!isValidPassword(password)) { // Your method to check for a valid password
            passwordLayout.setError(getString(R.string.error_weak_password));
            isPasswordValid = false;
        } else {
            passwordLayout.setError(null); // Clear previous error
            passwordLayout.setErrorEnabled(false);
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError(getString(R.string.error_required_confirmation_password));
            isConfirmPasswordValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError(getString(R.string.error_passwords_mismatch));
            isConfirmPasswordValid = false;
        } else {
            confirmPasswordLayout.setError(null); // Clear previous error
            confirmPasswordLayout.setErrorEnabled(false);
        }

        // Return true only if all validations are passed
        return isUsernameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid;
    }


    private void performRegistration() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Here, you would make a network request to your server to perform the registration.
        // This is typically done asynchronously.
        // For example, using a library like Retrofit to handle HTTP requests.

        // On a successful response, you might transition to another activity, or do other appropriate actions.
        // On a failure, you would display an appropriate error message to the user.
    }
}