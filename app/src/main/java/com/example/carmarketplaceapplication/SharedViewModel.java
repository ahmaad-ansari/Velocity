package com.example.carmarketplaceapplication;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

// ViewModel class shared between fragments or components
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<CarListModel> carModel = new MutableLiveData<>(); // CarListModel live data

    // Set the CarListModel value
    public void setCarListModel(CarListModel model) {
        carModel.setValue(model);
    }

    // Get the CarListModel live data
    public LiveData<CarListModel> getCarListModel() {
        return carModel;
    }

    // Live data to handle a list of objects (String URLs or Uri objects)
    private MutableLiveData<List<Object>> imageSources = new MutableLiveData<>();

    // Get the live data of image sources
    public LiveData<List<Object>> getImageSources() {
        return imageSources;
    }

    // Set the list of image sources
    public void setImageSources(List<Object> sources) {
        imageSources.setValue(sources);
    }

    // Resets the list to a state with placeholders (empty strings)
    public void clearImageSources() {
        List<Object> emptySourcesList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            emptySourcesList.add(""); // Use an empty string as a placeholder
        }
        imageSources.postValue(emptySourcesList);
    }

    // Reset the CarListModel value to null
    public void clearCarModel() {
        carModel.setValue(null);
    }
}
