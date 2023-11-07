package com.example.carmarketplaceapplication;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressAutocompleteHelper {
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    private String apiKey;
    private OpenCageService service; // Make service a field

    public AddressAutocompleteHelper(Context context, AutoCompleteTextView autoCompleteTextView) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
        this.apiKey = "e9dbca8a391c489b84669d7288342f0c";
        setupAddressAutocomplete();
    }


    private void setupAddressAutocomplete() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.opencagedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialize service
        service = retrofit.create(OpenCageService.class);

        // Initial call to add TextWatcher
        addTextWatcher();
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Implementation
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 3) {
                fetchAutocompleteSuggestions(service, s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Implementation
        }
    };

    private void addTextWatcher() {
        autoCompleteTextView.addTextChangedListener(textWatcher);
    }

    private void fetchAutocompleteSuggestions(OpenCageService service, String query) {
        service.autocomplete(apiKey, query, "en", "ca") // "ca" for Canada
                .enqueue(new Callback<OpenCageResponse>() {
                    @Override
                    public void onResponse(Call<OpenCageResponse> call, Response<OpenCageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<String> suggestions = new ArrayList<>();
                            for (OpenCageResponse.OpenCageResult result : response.body().results) {
                                suggestions.add(result.getFormatted());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, suggestions);
                            autoCompleteTextView.setAdapter(adapter);
                            if (suggestions.size() > 0) {
                                autoCompleteTextView.showDropDown();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OpenCageResponse> call, Throwable t) {
                        // Handle failure
                    }
                });
    }
}
