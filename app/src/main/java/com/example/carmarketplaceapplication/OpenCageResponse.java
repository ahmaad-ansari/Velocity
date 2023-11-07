package com.example.carmarketplaceapplication;

import java.util.List;

public class OpenCageResponse {
    public List<OpenCageResult> results;

    public static class OpenCageResult {
        public String formatted; // The formatted address

        public String getFormatted() {
            return formatted;
        }
    }
}


