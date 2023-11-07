package com.example.carmarketplaceapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenCageService {
    @GET("geocode/v1/json")
    Call<OpenCageResponse> autocomplete(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("language") String language,
            @Query("countrycode") String countryCode
    );
}




