package com.example.carmarketplaceapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupSignupPrompt();
    }

    private void setupSignupPrompt() {
        TextView signupText = findViewById(R.id.signup_text);

        SpannableString spannable = new SpannableString("Don't have an account? Sign up now!");

        // Color for the "Sign up now!" part
        int linkColor = ContextCompat.getColor(this, R.color.link_text);
        spannable.setSpan(
                new ForegroundColorSpan(linkColor),
                23,  // start index
                35,  // end index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Making the "Sign up now!" part clickable
        spannable.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // Handle the sign-up action here, like opening a sign-up activity or a webpage
                    }
                },
                23,  // start index
                35,  // end index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        signupText.setText(spannable);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());  // To make the clickable span responsive
    }
}