package com.example.carmarketplaceapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, passwordInput;
    private TextInputLayout usernameLayout, passwordLayout;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupSignupPrompt();
        // Initialize the fields and layouts
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        usernameLayout = findViewById(R.id.username_layout);
        passwordLayout = findViewById(R.id.password_layout);

        // Initialize generic TextWatchers
        GenericTextWatcher usernameWatcher = new GenericTextWatcher(usernameLayout, username -> !username.isEmpty(), getString(R.string.error_required_username));
        GenericTextWatcher passwordWatcher = new GenericTextWatcher(passwordLayout, password -> !password.isEmpty(), getString(R.string.error_required_password));

        usernameInput.addTextChangedListener(usernameWatcher);
        passwordInput.addTextChangedListener(passwordWatcher);

        loginButton.setOnClickListener(new View.OnClickListener() {
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
        TextView signupText = findViewById(R.id.register_text);

        SpannableString spannable = new SpannableString("Don't have an account? Register now!");

        // Color for the "Sign up now!" part
        int linkColor = ContextCompat.getColor(this, R.color.link_text);
        spannable.setSpan(
                new ForegroundColorSpan(linkColor),
                23,  // start index
                36,  // end index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Making the "Register now!" part clickable
        spannable.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // 'LoginActivity.this' is used here to refer to the LoginActivity context
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                },
                23,  // start index
                36,  // end index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        signupText.setText(spannable);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());  // To make the clickable span responsive
    }

    private boolean areFieldsValid() {
        // Retrieve the values from the input fields
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Flags indicating whether fields are valid
        boolean isUsernameValid = true, isPasswordValid = true;

        // Check if the fields are empty and set appropriate error messages
        if (username.isEmpty()) {
            usernameLayout.setError(getString(R.string.error_required_username));
            isUsernameValid = false;
        } else {
            usernameLayout.setError(null); // Clear previous error
            usernameLayout.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.error_required_password));
            isPasswordValid = false;
        } else {
            passwordLayout.setError(null); // Clear previous error
            passwordLayout.setErrorEnabled(false);
        }

        // Return true only if all validations are passed
        return isUsernameValid && isPasswordValid;
    }

    private void performRegistration() {
        String email = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Here, you would make a network request to your server to perform the registration.
        // This is typically done asynchronously.
        // For example, using a library like Retrofit to handle HTTP requests.

        // On a successful response, you might transition to another activity, or do other appropriate actions.
        // On a failure, you would display an appropriate error message to the user.
    }
}