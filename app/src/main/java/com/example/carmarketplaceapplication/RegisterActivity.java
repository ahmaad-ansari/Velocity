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
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupSignupPrompt();
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
}