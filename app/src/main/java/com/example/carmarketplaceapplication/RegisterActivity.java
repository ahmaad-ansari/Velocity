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

    private TextInputEditText emailInput, passwordInput, confirmPasswordInput;
    private TextInputLayout emailLayout, passwordLayout, confirmPasswordLayout;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupSignupPrompt();

        // Initialize the fields and layouts
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);

        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);

        // Setup the TextWatchers for real-time validation
        emailInput.addTextChangedListener(createTextWatcherForEmail());
        passwordInput.addTextChangedListener(createTextWatcherForPassword());
        confirmPasswordInput.addTextChangedListener(createTextWatcherForConfirmPassword());

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

    private TextWatcher createTextWatcherForEmail() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidEmail(charSequence.toString())) {
                    emailLayout.setError("Invalid email format");
                } else {
                    emailLayout.setError(null); // Clear the error
                    emailLayout.setErrorEnabled(false); // Disable the error, so it removes the extra space
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    private TextWatcher createTextWatcherForPassword() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidPassword(charSequence.toString())) {
                    passwordLayout.setError("Password too weak");
                } else {
                    passwordLayout.setError(null); // Clear the error
                    passwordLayout.setErrorEnabled(false); // Disable the error, so it removes the extra space
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    private TextWatcher createTextWatcherForConfirmPassword() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = passwordInput.getText().toString();
                if (!charSequence.toString().equals(password)) {
                    confirmPasswordLayout.setError("Passwords do not match");
                } else {
                    confirmPasswordLayout.setError(null); // Clear the error
                    confirmPasswordLayout.setErrorEnabled(false); // Disable the error, so it removes the extra space
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
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
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Validate the fields
        boolean emailIsValid = isValidEmail(email);
        boolean passwordIsValid = isValidPassword(password);
        boolean passwordsMatch = password.equals(confirmPassword);

        // Maybe update error messages if needed
        if (!emailIsValid) {
            emailLayout.setError("Invalid email format");
        }
        if (!passwordIsValid) {
            passwordLayout.setError("Password too weak");
        }
        if (!passwordsMatch) {
            confirmPasswordLayout.setError("Passwords do not match");
        }

        // Determine if all validations pass
        return emailIsValid && passwordIsValid && passwordsMatch;
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