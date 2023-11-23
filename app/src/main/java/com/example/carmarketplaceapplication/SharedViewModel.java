package com.example.carmarketplaceapplication;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<CarListModel> carModel = new MutableLiveData<>();

    public void setCarListModel(CarListModel model) {
        carModel.setValue(model);
    }

    public LiveData<CarListModel> getCarListModel() {
        return carModel;
    }

    // Updated to handle a list of objects (either String URLs or Uri objects)
    private MutableLiveData<List<Object>> imageSources = new MutableLiveData<>();

    public LiveData<List<Object>> getImageSources() {
        return imageSources;
    }

    public void setImageSources(List<Object> sources) {
        imageSources.setValue(sources);
    }

    // Resets the list to a state with placeholders (empty strings)
    public void clearImageSources() {
        List<Object> emptySourcesList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            emptySourcesList.add(""); // Using an empty string as a placeholder
        }
        imageSources.postValue(emptySourcesList);
    }

    public void clearCarModel() {
        carModel.setValue(null);
    }
}
